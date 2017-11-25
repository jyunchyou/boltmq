package io.openmessaging.client.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by fbhw on 17-10-31.
 */
public class FactoryProducer {

    Logger logger = LoggerFactory.getLogger(FactoryProducer.class);

    public AbstractProducer createProducer(Properties implProperties){

        AbstractProducer mqProducer = null;
        try {
            mqProducer = new AbstractProducer();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        mqProducer.setImplProperties(implProperties);
        return mqProducer;



    }

}
