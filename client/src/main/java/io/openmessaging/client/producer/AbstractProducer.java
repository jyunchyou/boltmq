package io.openmessaging.client.producer;

import io.openmessaging.client.common.CallBack;
import io.openmessaging.client.impl.MessageQueue;
import io.openmessaging.client.impl.MessageQueues;
import io.openmessaging.client.net.SendResult;
import io.openmessaging.client.selector.QueueSelectByHash;
import io.openmessaging.client.selector.QueueSelectByRandom;
import io.openmessaging.client.selector.QueueSelector;
import io.openmessaging.client.impl.MessageImpl;
import io.openmessaging.client.impl.PropertiesImpl;

import java.io.IOException;
import java.util.List;

/**
 * Created by fbhw on 17-10-31.
 */
public class AbstractProducer {


    private PropertiesImpl implProperties = null;

    private KernelClient kernelClient = null;

    private QueueSelectByHash queueSelectByHash = new QueueSelectByHash();

    private QueueSelectByRandom queueSelectByRandom = new QueueSelectByRandom();

    private MessageQueues messageQueues = new MessageQueues();

    public AbstractProducer() throws IOException {
    }


    //同步发送
    public SendResult send(MessageImpl message){

       return  send(message,null);

    }

    //开启定时任务
    public void start(){

    }

    public SendResult send(MessageImpl message, QueueSelector queueSelector){

        return this.send(message,3,queueSelector);

    }

    public SendResult send(MessageImpl message,int delayTime){
        return  this.send(message,delayTime,null);

    }
    //delayTime支持固定时长,传入delay等级
    public SendResult send(MessageImpl message,int delayTime,Object shardingKey){

        MessageQueue messageQueue = null;

        List list = messageQueues.getList();
        if (list == null) {
            messageQueues.updateMessageQueuesFromNameServer();
            list = messageQueues.getList();
        }
        if (shardingKey == null) {
            messageQueue = queueSelectByRandom.select(list);
        }else {
            messageQueue = queueSelectByHash.select(list,shardingKey);
        }
        return kernelClient.send(message,delayTime,messageQueue,implProperties);
    }



    //异步发送
    public void asyncSend(MessageImpl message, CallBack callBack){

        this.asyncSend(message,callBack,null);
    }

    public void asyncSend(MessageImpl message, CallBack callBack,QueueSelector queueSelector){

    }
    //单向发送
    public void onewaySend(MessageImpl message){

        this.onewaySend(message,null);

    }

    public void onewaySend(MessageImpl message, QueueSelector queueSelector){


    }


    public PropertiesImpl getImplProperties() {
        return implProperties;
    }

    public void setImplProperties(PropertiesImpl implProperties) {
        this.implProperties = implProperties;
    }
}
