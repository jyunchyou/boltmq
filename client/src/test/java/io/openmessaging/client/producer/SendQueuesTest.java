package io.openmessaging.client.producer;

import org.junit.Assert;
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
        System.out.println(list);

    }
}
