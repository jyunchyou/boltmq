package io.openmessaging.client.producer;

import io.openmessaging.client.impl.PropertiesImpl;

import java.io.IOException;

/**
 * Created by fbhw on 17-10-31.
 */
public class FactoryProducer {

    public AbstractProducer createProducer(PropertiesImpl implProperties){

        AbstractProducer mqProducer = null;
        try {
            mqProducer = new AbstractProducer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mqProducer.setImplProperties(implProperties);
        return mqProducer;



    }

}
