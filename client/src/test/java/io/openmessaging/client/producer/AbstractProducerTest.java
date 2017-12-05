package io.openmessaging.client.producer;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by fbhw on 17-12-1.
 */
public class AbstractProducerTest {

    Logger logger = LoggerFactory.getLogger(AbstractProducerTest.class);

    AbstractProducer abstractProducer = null;


    @Before
    public void init(){
        try {
            this.abstractProducer = new AbstractProducer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSend(){

    }

    @Test
    public void start(){

        abstractProducer.start();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
