package io.openmessaging.client.producer;

import io.openmessaging.client.common.DefaultMessage;
import io.openmessaging.client.common.DefaultProperties;

/**
 * Created by fbhw on 17-10-31.
 */
public class FactoryProducer {

    public MQProducer createProducer(DefaultProperties defaultProperties){

        MQProducer mqProducer = new MQProducer();
        mqProducer.setDefaultProperties(defaultProperties);
        return mqProducer;



    }

}
