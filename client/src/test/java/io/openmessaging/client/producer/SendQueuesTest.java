package io.openmessaging.client.producer;

import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbhw on 17-12-2.
 */
public class SendQueuesTest {

    private Logger logger = LoggerFactory.getLogger(SendQueuesTest.class);

    private SendQueues sendQueues;

    @Before
    public void init(){
        try {
            sendQueues = new SendQueues();
        } catch (IOException e) {
            logger.error("new SendQueues() exception");
            e.printStackTrace();
        }
    }

    @Test
    public void testListSize(){
        List list = new ArrayList(5);

        logger.info(String.valueOf(list.size()));

    }

    @Test
    public void testGetList(){
        List list = sendQueues.getList();
        SendQueue sendQueue = (SendQueue) list.get(0);

       // System.out.println(sendQueue.getBrokerInfo().getIp()+sendQueue.getBrokerInfo().getPort());


    }
}
