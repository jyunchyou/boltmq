    package io.openmessaging.client.producer;

    import com.alibaba.nacos.api.exception.NacosException;
    import com.alibaba.nacos.api.naming.pojo.Instance;
    import com.fasterxml.jackson.databind.ser.Serializers;
    import com.google.common.primitives.Longs;
    import io.netty.buffer.ByteBuf;
    import io.netty.channel.Channel;
    import io.netty.channel.ChannelFuture;
    import io.openmessaging.client.common.SendCallBack;
    import io.openmessaging.client.constant.ConstantClient;
    import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.exception.OutOfByteBufferException;
    import io.openmessaging.client.net.*;
    import io.openmessaging.client.selector.QueueSelectByHash;
import io.openmessaging.client.selector.QueueSelectByRandom;
import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;
    import io.openmessaging.client.util.InfoCast;
    import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
    import java.lang.management.ManagementFactory;
    import java.net.InetAddress;
    import java.net.NetworkInterface;
    import java.net.SocketException;
    import java.net.UnknownHostException;
    import java.util.Map;
    import java.util.UUID;
    import java.util.concurrent.ConcurrentHashMap;
    import java.util.concurrent.atomic.AtomicInteger;
    import java.util.concurrent.atomic.AtomicLong;

    /**
     * Created by fbhw on 17-10-31.
     */
    public class AbstractProducer {

        int sum = 0;

        Logger logger = LoggerFactory.getLogger(AbstractProducer.class);

        private BProperties implBProperties = null;

        private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

        private KernelProducer kernelProducer = new KernelProducer();

        private QueueSelectByHash queueSelectByHash = new QueueSelectByHash();

        private QueueSelectByRandom queueSelectByRandom = new QueueSelectByRandom();

        private SendQueues sendQueues = new SendQueues();

        private ConcurrentHashMap<Channel,Long> sendOffsetMap = new ConcurrentHashMap();

        private long sendOffset = 0L;

        private ByteBuf[] sendConfirm = new ByteBuf[ConstantClient.SEND_CONFIRM_SIZE];

        private ConcurrentHashMap<Channel, Map> sendConfirmMap = new ConcurrentHashMap();

        static byte[] mac = null;


        public AbstractProducer() throws IOException {

        }


        public void send(BaseMessage baseMessage, CallBackListener callBackListener, boolean isDelay,
                         boolean isSeq, boolean isCallBack, boolean isOneWay, boolean isSyn, int conserveTime,
                         byte delayTime, String delayTimeUnit, Channel channel) throws InterruptedException, SocketException, UnknownHostException {
            //初始化消息

            //System.out.println(sum++);
            CallBackMap.semaphore.acquire();
            baseMessage.setDelay(isDelay);
            baseMessage.setSeq(isSeq);
            baseMessage.setCallBack(isCallBack);
            baseMessage.setOneWay(isOneWay);
            baseMessage.setSyn(isSyn);
            baseMessage.setConserveTime(conserveTime);
            baseMessage.setDelayTime(delayTime);
            baseMessage.setDelayTimeUnit(delayTimeUnit);
            //设置回调
            CallBackMap.add(baseMessage.getTopic(), callBackListener);

            ByteBuf byteBuf = encodeAndDecode.encodeBaseMessage(baseMessage,this,channel);
            ChannelFuture channelFuture = null;
            if (isSyn) {
                channelFuture = channel.writeAndFlush(byteBuf);
                //channel.flush();
            }else{
                channelFuture = channel.writeAndFlush(byteBuf);
                //channel.flush();
            }

            //channelFuture.sync();






        }



        //同步发送
        public void send(Message message) throws NacosException {
            this.send(message,0,null,null,false);
        }

        //顺序消息
        public void send(Message message,Object shardingKey) throws NacosException {
             this.send(message,0,shardingKey,null,false);
        }

        //TODO 延时消息
        public void send(Message message, int delayTime) throws NacosException {
            this.send(message,delayTime,null,null,false);
        }

        //异步发送
        public void send(Message message,SendCallBack sendCallBack) throws NacosException {

            this.send(message,0,null,sendCallBack,false);
        }

        //异步顺序发送
        public void send(Message message, SendCallBack callBack , Object shardingKey ) throws NacosException {

            this.send(message,0,shardingKey,callBack,false);

        }

        //单向发送
        public void send(Message message,boolean oneway) throws NacosException {

            this.send(message,0,null,null,oneway);
        }
        public void send(Message message,int delayTime,Object shardingKey,SendCallBack sendCallBack,boolean oneWay) throws NacosException {

            Instance instance = FactoryProducer.naming.selectOneHealthyInstance("brocker");


        SendResult sendResult = null;
            SendQueue sendQueue = InfoCast.cast(instance);
 /*           try {
             kernelProducer.send(message,delayTime,sendQueue, implBProperties,sendCallBack,oneWay);
            } catch (OutOfBodyLengthException e) {

                e.printStackTrace();
                logger.error("out of bodyLength exception");
            } catch (OutOfByteBufferException e) {
                logger.error("out of byteBuffer exception");
                e.printStackTrace();
            }
 */           return;

        }

        //开启定时任务
        public void start(){
//            kernelProducer.start(sendQueues);

        }



        public BProperties getImplProperties() {
            return implBProperties;
        }

        public void setImplProperties(BProperties implBProperties) {
            this.implBProperties = implBProperties;
        }

        public static byte[] getMessageId() throws UnknownHostException, SocketException {
            InetAddress ia;
                // 获取本地IP对象
                ia = InetAddress.getLocalHost();
                // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
               if (mac == null) {
                   mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
               }
                byte[] time = Longs.toByteArray(System.currentTimeMillis());
                String name = ManagementFactory.getRuntimeMXBean().getName();

// get pid
                String pid = name.split("@")[0];
                byte[] threadId = Longs.toByteArray(Thread.currentThread().getId());
                byte[] messageId = new byte[time.length + mac.length + threadId.length];
            System.arraycopy(mac, 0, messageId, 0, mac.length);
            System.arraycopy(time, 0, messageId, mac.length, time.length);
            System.arraycopy(threadId,0,messageId,time.length + mac.length,threadId.length);
                return messageId;
        }

        public static void main(String[] args) throws IOException {

            System.out.println(AbstractProducer.getMessageId().length);

        }




        public ConcurrentHashMap<Channel,Map> getSendConfirmMap() {
            return sendConfirmMap;
        }

        public void setSendConfirmMap(ConcurrentHashMap sendConfirmMap) {
            this.sendConfirmMap = sendConfirmMap;
        }


        public ByteBuf[] getSendConfirm() {
            return sendConfirm;
        }

        public void setSendConfirm(ByteBuf[] sendConfirm) {
            this.sendConfirm = sendConfirm;
        }

        public long getSendOffset() {
            return sendOffset;
        }

        public void setSendOffset(long sendOffset) {
            this.sendOffset = sendOffset;
        }

        public ConcurrentHashMap<Channel, Long> getSendOffsetMap() {
            return sendOffsetMap;
        }

        public void setSendOffsetMap(ConcurrentHashMap<Channel, Long> sendOffsetMap) {
            this.sendOffsetMap = sendOffsetMap;
        }
    }
