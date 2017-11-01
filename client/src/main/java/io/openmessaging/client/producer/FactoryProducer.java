package io.openmessaging.client.producer;

import io.openmessaging.client.impl.PropertiesImpl;

/**
 * Created by fbhw on 17-10-31.
 */
public class FactoryProducer {

    public AbstractProducer createProducer(PropertiesImpl implProperties){

        AbstractProducer mqProducer = new AbstractProducer();
        mqProducer.setImplProperties(implProperties);
        return mqProducer;



    }

}
