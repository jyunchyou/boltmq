    package io.openmessaging.client.producer;

    import io.openmessaging.client.common.CallBack;
    import io.openmessaging.client.exception.OutOfBodyLengthException;
    import io.openmessaging.client.exception.OutOfByteBufferException;
    import io.openmessaging.client.net.SendResult;
    import io.openmessaging.client.selector.QueueSelectByHash;
    import io.openmessaging.client.selector.QueueSelectByRandom;
    import io.openmessaging.client.selector.QueueSelector;
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
        public SendResult send(Message message){

           return  send(message,null);

        }

        //开启定时任务
        public void start(){
            kernelProducer.start(sendQueues);

        }

        public SendResult send(Message message,Object shardingKey){

            return this.send(message,3,null);

        }

        public SendResult send(Message message, int delayTime){
            return  this.send(message,delayTime,null);

        }
        //delayTime支持固定时长,传入delay等级
        public SendResult send(Message message,int delayTime,Object shardingKey){

            SendQueue sendQueue = null;

            List list = sendQueues.getList();

            if (shardingKey == null) {
                sendQueue = queueSelectByRandom.select(list);
            }else {
                sendQueue = queueSelectByHash.select(list,shardingKey);
            }


            SendResult sendResult = null;
            try {
                sendResult = kernelProducer.send(message,delayTime,sendQueue,implProperties);
            } catch (OutOfBodyLengthException e) {

                e.printStackTrace();
                logger.error("out of bodyLength exception");
            } catch (OutOfByteBufferException e) {
                logger.error("out of byteBuffer exception");
                e.printStackTrace();
            }
            return sendResult;

        }



        //异步发送
        public void asyncSend(Message message, CallBack callBack){

            this.asyncSend(message,callBack,null);
        }

        public void asyncSend(Message message, CallBack callBack, QueueSelector queueSelector){

        }
        //单向发送
        public void onewaySend(Message message){

            this.onewaySend(message,null);

        }

        public void onewaySend(Message message, QueueSelector queueSelector){


        }


        public Properties getImplProperties() {
            return implProperties;
        }

        public void setImplProperties(Properties implProperties) {
            this.implProperties = implProperties;
        }
    }
