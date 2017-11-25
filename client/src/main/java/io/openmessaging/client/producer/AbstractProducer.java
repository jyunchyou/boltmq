package io.openmessaging.client.producer;

import io.openmessaging.client.common.CallBack;
import io.openmessaging.client.net.SendResult;
import io.openmessaging.client.selector.QueueSelectByHash;
import io.openmessaging.client.selector.QueueSelectByRandom;
import io.openmessaging.client.selector.QueueSelector;

import java.io.IOException;
import java.util.List;

/**
 * Created by fbhw on 17-10-31.
 */
public class AbstractProducer {


    private Properties implProperties = null;

    private KernelProducer kernelProducer = null;

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
        kernelProducer.start(String.valueOf(
                implProperties.getProperties("nameSvrAddress")
        ));

    }

    public SendResult send(Message message, QueueSelector queueSelector){

        return this.send(message,3,queueSelector);

    }

    public SendResult send(Message message, int delayTime){
        return  this.send(message,delayTime,null);

    }
    //delayTime支持固定时长,传入delay等级
    public SendResult send(Message message, int delayTime, Object shardingKey){

        SendQueue sendQueue = null;

        List list = sendQueues.getList();
        if (list == null) {
            kernelProducer.updateMessageQueuesFromNameServer();
            list = sendQueues.getList();
        }
        if (shardingKey == null) {
            sendQueue = queueSelectByRandom.select(list);
        }else {
            sendQueue = queueSelectByHash.select(list,shardingKey);
        }
        return kernelProducer.send(message,delayTime, sendQueue,implProperties);
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
