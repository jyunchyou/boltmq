package io.openmessaging.consumer.consumer;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.constant.ConsumeModel;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractConsumer {

    Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);

    private Properties implProperties = null;

    private KernelConsumer kernelConsumer = KernelConsumer.getKernelConsumer();

    private ReceiveMessageTable receiveMessageTable = new ReceiveMessageTable();

    private String topic = null;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private ConsumeModel consumeModel = ConsumeModel.GROUP;//默认集群消费,点到点


    public AbstractConsumer(){

    }

    public void setConcumeModel(ConsumeModel concumeModel){

        this.consumeModel = concumeModel;

    }



    public void subscribe(String topic, ListenerMessage listenerMessage){

        subscribe(topic,listenerMessage, ConstantConsumer.PULL_BUFFER_SIZE);

    }

    public void subscribe(String topic, ListenerMessage listenerMessage,int num){
        this.topic = topic;
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

        if (topic == null) {
            throw new NullPointerException();

        }
        kernelConsumer.start(receiveMessageTable,topic);
    }


    public long generatingUniqId(){
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
        long uniqId = ipInt + port + currentTime + pid;

        return uniqId;

    }

    public Properties getImplProperties() {
        return implProperties;
    }

    public void setImplProperties(Properties implProperties) {
        this.implProperties = implProperties;
    }
}
