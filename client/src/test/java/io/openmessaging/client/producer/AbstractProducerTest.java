package io.openmessaging.client.producer;

import com.alibaba.nacos.api.exception.NacosException;
import io.openmessaging.client.exception.RegisterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by fbhw on 17-12-1.
 */
public class AbstractProducerTest {

    Logger logger = LoggerFactory.getLogger(AbstractProducerTest.class);

    AbstractProducer abstractProducer = null;


    //@Before
    public void init(){
        try {
            this.abstractProducer = new AbstractProducer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //@Test
    public void testSend(){

    }

    //@Test
    public void start(){

        abstractProducer.start();

        try {
            Thread.sleep(17000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testSendSync() throws RegisterException, NacosException, IOException {


        BProperties BProperties = new BProperties();
        BProperties.putProperties("key","value");



        AbstractProducer abstractProducer = FactoryProducer.createProducer();


        FactoryProducer.registry(BProperties,abstractProducer);




        long startTime = System.currentTimeMillis();
        for (int indexNum = 0;indexNum < 5000000;indexNum++) {
            Message message = new Message("TOPIC_01", "1", "发送成功".getBytes());

            abstractProducer.send(message);


        }
        long endTime = System.currentTimeMillis();



        float tpsMillis = (5000000l/(float)(endTime - startTime));
          System.out.println("tps:"+tpsMillis * 1000+"t/s");




        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
