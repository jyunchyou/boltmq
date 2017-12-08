package io.openmessaging.consumer.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by fbhw on 17-12-7.
 */
public class FactoryConsumer {

    Logger logger = LoggerFactory.getLogger(FactoryConsumer.class);

    public static AbstractConsumer createProducer(Properties implProperties){


        AbstractConsumer mqProducer = null;
        mqProducer = new AbstractConsumer();
        mqProducer.setImplProperties(implProperties);
        return mqProducer;



    }
}
