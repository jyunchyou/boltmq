package io.openmessaging.client.producer;

import io.openmessaging.client.common.CallBack;
import io.openmessaging.client.net.SendResult;
import io.openmessaging.client.selector.QueueSelector;
import io.openmessaging.client.impl.MessageImpl;
import io.openmessaging.client.impl.PropertiesImpl;

/**
 * Created by fbhw on 17-10-31.
 */
public class AbstractProducer {


    private PropertiesImpl implProperties = null;
    //同步发送
    public SendResult send(MessageImpl message){

       return  send(message,null);

    }

    public SendResult send(MessageImpl message, QueueSelector queueSelector){

        return this.send(message,3,queueSelector);

    }

    public SendResult send(MessageImpl message,int delayTime){
        return  this.send(message,delayTime,null);

    }
    //delayTime支持固定时长,传入delay等级
    public SendResult send(MessageImpl message,int delayTime, QueueSelector queueSelector){

        return null;
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
