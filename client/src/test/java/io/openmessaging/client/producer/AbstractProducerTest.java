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
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendSync(){

       FactoryProducer factoryProducer = new FactoryProducer();

        Properties properties = new Properties();
        properties.putProperties("key","value");

        AbstractProducer abstractProducer = factoryProducer.createProducer(properties);




        Message message = new Message("TOPIC_1","1","发送成功！".getBytes());





        abstractProducer.send(message);

        try {
            Thread.sleep(17000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
