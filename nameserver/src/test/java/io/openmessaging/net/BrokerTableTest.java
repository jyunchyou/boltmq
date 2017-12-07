package io.openmessaging.net;

import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.table.MessageInfo;
import io.openmessaging.table.MessageInfoQueue;
import io.openmessaging.table.MessageInfoQueues;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-7.
 */
public class BrokerTableTest {

    Logger logger = LoggerFactory.getLogger(NettyServerTest.class);
    private NettyServer nettyServer = null;

    @Before
    public void init(){
        nettyServer = new NettyServer();

    }

    @Test
    public void testConn(){

        nettyServer.bindBroker(ConstantNameServer.RECEIVE_FROM_BROKER_PORT);


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        MessageInfoQueue messageInfoQueue = (MessageInfoQueue) MessageInfoQueues.concurrentHashMap.get(0);

        MessageInfo messageInfo = (MessageInfo) messageInfoQueue.getList().get(0);

        System.out.println("---"+messageInfo.getLen());
        System.out.println("---"+messageInfo.getOffset());
        System.out.println("---"+messageInfo.getTopic());
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
