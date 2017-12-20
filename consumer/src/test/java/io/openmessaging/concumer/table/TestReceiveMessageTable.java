package io.openmessaging.concumer.table;

import io.openmessaging.consumer.consumer.AbstractConsumer;
import io.openmessaging.consumer.consumer.FactoryConsumer;
import io.openmessaging.consumer.consumer.Message;
import io.openmessaging.consumer.consumer.Properties;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by fbhw on 17-12-7.
 */
public class TestReceiveMessageTable {

    Logger logger = LoggerFactory.getLogger(TestReceiveMessageTable.class);

    private ReceiveMessageTable receiveMessageTable = null;

    private AbstractConsumer abstractConsumer = null;

    @Before
    public void init(){

        abstractConsumer = FactoryConsumer.createProducer(new Properties());


        receiveMessageTable = new ReceiveMessageTable();
    }

    @Test
    public void testUpdateReceiveTableFromNameServer() throws InterruptedException {




        new Thread(new Runnable() {
            public void run() {



                abstractConsumer.subscribe("TOPIC_01", new ListenerMessage() {
                    public void listener(Message message) {

                    }

                    public void listener(List<Message> list) {

                        System.out.println("消费成功");
                    }


                });

            }
        }).start();
        abstractConsumer.start();









        Thread.sleep(10000000);

    }




}
