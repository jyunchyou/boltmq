    package io.openmessaging.client.producer;

    import io.openmessaging.client.common.SendCallBack;
import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.exception.OutOfByteBufferException;
import io.openmessaging.client.net.SendResult;
import io.openmessaging.client.selector.QueueSelectByHash;
import io.openmessaging.client.selector.QueueSelectByRandom;
import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

    /**
     * Created by fbhw on 17-10-31.
     */
    public class AbstractProducer {

        Logger logger = LoggerFactory.getLogger(AbstractProducer.class);

        private Properties implProperties = null;

        private KernelProducer kernelProducer = new KernelProducer();

        private QueueSelectByHash queueSelectByHash = new QueueSelectByHash();

        private QueueSelectByRandom queueSelectByRandom = new QueueSelectByRandom();

        private SendQueues sendQueues = new SendQueues();


        public AbstractProducer() throws IOException {

        }

        //同步发送
        public void send(Message message){
            this.send(message,0,null,null,false);
        }

        //顺序消息
        public void send(Message message,Object shardingKey){
             this.send(message,0,shardingKey,null,false);
        }

        //TODO 延时消息
        public void send(Message message, int delayTime){
            this.send(message,delayTime,null,null,false);
        }

        //异步发送
        public void send(Message message,SendCallBack sendCallBack){

            this.send(message,0,null,sendCallBack,false);
        }

        //异步顺序发送
        public void send(Message message, SendCallBack callBack , Object shardingKey ){

            this.send(message,0,shardingKey,callBack,false);

        }

        //单向发送
        public void send(Message message,boolean oneway){

            this.send(message,0,null,null,oneway);
        }



        //
        public void send(Message message,int delayTime,Object shardingKey,SendCallBack sendCallBack,boolean oneWay){

            SendQueue sendQueue = null;

            List<SendQueue> list = sendQueues.getList();

            while (list.size() == 0) {
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



            if (shardingKey == null) {
                sendQueue = queueSelectByRandom.select(list);
            }else {
                sendQueue = queueSelectByHash.select(list,shardingKey);
            }


            SendResult sendResult = null;
            try {
             kernelProducer.send(message,delayTime,sendQueue,implProperties,sendCallBack,oneWay);
            } catch (OutOfBodyLengthException e) {

                e.printStackTrace();
                logger.error("out of bodyLength exception");
            } catch (OutOfByteBufferException e) {
                logger.error("out of byteBuffer exception");
                e.printStackTrace();
            }
            return;

        }

        //开启定时任务
        public void start(){
            kernelProducer.start(sendQueues);

        }



        public Properties getImplProperties() {
            return implProperties;
        }

        public void setImplProperties(Properties implProperties) {
            this.implProperties = implProperties;
        }
    }
