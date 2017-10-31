package io.openmessaging.client.producer;

import io.openmessaging.client.selector.QueueSelector;
import io.openmessaging.client.common.DefaultMessage;
import io.openmessaging.client.common.DefaultProperties;

/**
 * Created by fbhw on 17-10-31.
 */
public class MQProducer {


    private DefaultProperties defaultProperties = null;
    //同步发送
    public void send(DefaultMessage defaultMessage){

        send(defaultMessage,null);

    }

    public void send(DefaultMessage defaultMessage,QueueSelector queueSelector){


    }

    //异步发送
    public void asyncSend(DefaultMessage defaultMessage){

    }

    public void asyncSend(DefaultMessage defaultMessage,QueueSelector queueSelector){

    }
    //单向发送
    public void onewaySend(DefaultMessage defaultMessage){


    }

    public void onewaySend(DefaultMessage defaultMessage,QueueSelector queueSelector){


    }


    public DefaultProperties getDefaultProperties() {
        return defaultProperties;
    }

    public void setDefaultProperties(DefaultProperties defaultProperties) {
        this.defaultProperties = defaultProperties;
    }
}
