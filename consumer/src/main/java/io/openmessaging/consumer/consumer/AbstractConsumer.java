package io.openmessaging.consumer.consumer;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.constant.ConsumeModel;
import io.openmessaging.consumer.exception.RegisterException;
import io.openmessaging.consumer.handler.ReceiveMessageHandlerAdapter;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.net.EncodeAndDecode;
import io.openmessaging.consumer.net.NettyConsumer;
import io.openmessaging.consumer.table.ConsumerBrokerMap;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractConsumer {

    //Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);

    private KernelConsumer kernelConsumer = KernelConsumer.getKernelConsumer();

    private ReceiveMessageTable receiveMessageTable = new ReceiveMessageTable();

    private String topic = null;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private ConsumeModel consumeModel = ConsumeModel.GROUP;//默认集群消费,点到点

    private MappedByteBuffer mappedByteBuffer = null;

    private Semaphore semaphore = new Semaphore(ConstantConsumer.PULL_SEMAPhORE_NUM);

    private long uniqId = 0;

    private ReceiveMessageHandlerAdapter receiveMessageHandlerAdapter = null;

    private ConsumeCallBack consumeCallBack = null;

    private long consumeIndex = 1;

    public AbstractConsumer(String topic,String instanceId,ConsumeCallBack consumeCallBack) throws IOException, InterruptedException {

        NettyConsumer.consumeCallBackMap.put(instanceId,consumeCallBack);
        this.consumeCallBack = consumeCallBack;

        this.topic = topic;
        //初始化消费下标文件,结构为一个topic-多个brokerId,每个brokerId一个文件

        File consumeAdd = new File(ConstantConsumer.CONSUME_INDEX_FILE_ADDRESS + topic);
        if (!consumeAdd.exists()) {
            consumeAdd.mkdirs();
        }


        List<Instance> list = ConsumerBrokerMap.consumerBrokerMap.get(instanceId);
        for (Instance instance : list) {
            File consumeFile = new File(ConstantConsumer.CONSUME_INDEX_FILE_ADDRESS + topic + "\\" + instance.getInstanceId());
            if (!consumeFile.exists()) {
                consumeFile.createNewFile();
            }


            mappedByteBuffer = new RandomAccessFile(consumeFile, "rw").getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8);


            mappedByteBuffer.position(0);
            long index = mappedByteBuffer.getLong();
            if (index == 0) {
                index = 1;
            }
            NettyConsumer.consumeIndexMap.put(topic + instance.getInstanceId(),index);
        }

    }

    public void setConcumeModel(ConsumeModel concumeModel){
        this.consumeModel = concumeModel;
    }
    public void shardingBroker(int num){
    }
    public void pull(String instanceId) throws InterruptedException {

        //控制每个连接pull请求挂起的数量



        consumeCallBack.semaphore.acquire();

        try {
            List<Instance> list = ConsumerBrokerMap.consumerBrokerMap.get(instanceId);
            if (list == null || list.size() == 0) {
                return;
            }


            for (Instance instance : list) {

                String key = topic + instance.getInstanceId();
                Channel channel = NettyConsumer.channelMap.get(topic + instance.getInstanceId());
                Set<String> s = NettyConsumer.channelMap.keySet();
                for (String str : s) {
                }
                if (channel == null) {
                    return;
                }

                //根据获取消费下标
                Long consumeIndex = NettyConsumer.consumeIndexMap.get(key);

                if (consumeIndex == null) {
                    //新增的broker,增加消费索引，和持久化文件

                    NettyConsumer.consumeIndexMap.put(topic + instance.getInstanceId(),1L);


                }
                ByteBuf byteBuf = EncodeAndDecode.encodePollConsumeBackge(topic, consumeCallBack.pullNum,consumeIndex);
                synSend(channel,byteBuf);
                NettyConsumer.consumeIndexMap.put(key,consumeIndex + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void shardingPoll(){


    }


    public static synchronized void synSend(Channel channel,ByteBuf byteBuf){
        channel.writeAndFlush(byteBuf);

    }

    public void subscribe(String topic, ListenerMessage listenerMessage) throws NacosException, InterruptedException, RegisterException {

        subscribe(topic,listenerMessage, ConstantConsumer.PULL_BUFFER_SIZE);

    }

    public void subscribe(String topic, ListenerMessage listenerMessage,int num) throws RegisterException, NacosException, InterruptedException {
        this.topic = topic;




        //获取唯一id
        long uniqId;
        if (consumeModel == ConsumeModel.BROADCAST) {

            uniqId = generatingUniqId();
        }else {
            uniqId = ConstantConsumer.GROUP_ID;
        }
        kernelConsumer.subscribe(topic,listenerMessage,num,countDownLatch,uniqId);

    }
    //开启定时任务
    public void start(){

        while (topic == null) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        kernelConsumer.start(receiveMessageTable,topic);
    }


    public long generatingUniqId(){

        if (uniqId != 0) {
            return uniqId;
        }
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ip = inetAddress.getHostAddress();

        char[] ipChar = new char[ip.length()];
        for (int checkNum = 0,charAt = 0;checkNum < ip.length();checkNum ++) {
            char indexChar = ip.charAt((checkNum));
            if (indexChar == '.') {
                continue;

            }

            ipChar[charAt] = ip.charAt(checkNum);
            ++charAt;
        }
        String ipString = new String(ipChar).trim();
        int ipInt = Integer.parseInt(ipString);
        int port = ConstantConsumer.CONSUMER_PORT;
        long currentTime = System.currentTimeMillis();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pidString = name.split("@")[0];
        int pid = Integer.parseInt(pidString);
        long uId = ipInt + port + currentTime + pid;

        uniqId = uId;

        return uniqId;

    }



}
