package io.openmessaging.start;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.nameserver.NameServerInfo;
import io.openmessaging.net.NettyServer;
import io.openmessaging.net.NettyServerTest;
import io.openmessaging.store.MessageInfo;
import io.openmessaging.store.MessageInfoQueue;
import io.openmessaging.store.MessageInfoQueues;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractStartTest {

    Logger logger = LoggerFactory.getLogger(NettyServerTest.class);
    private AbstractStart abstractStart = null;


    @Before
    public void init(){
        abstractStart = new AbstractStart();


    }


    @Test
    public void testStart(){

        /*messageInfoQueues = new MessageInfoQueues();*/

        MessageInfoQueues.init(4);
        ConcurrentHashMap concurrentHashMap = MessageInfoQueues.concurrentHashMap;
        MessageInfoQueue messageInfoQueue = (MessageInfoQueue) concurrentHashMap.get("TOPIC_01");

        messageInfoQueue.setQueueId(1 + "");
        List list = messageInfoQueue.getList();
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setLen(123);
        messageInfo.setOffset(1234567);


        list.add(messageInfo);

       // System.out.println(list);


        MessageInfoQueue messageInfoQueue1 = (MessageInfoQueue) concurrentHashMap.get("TOPIC_02");
        messageInfoQueue1.setQueueId(2 + "");
        List list1 = messageInfoQueue1.getList();
        MessageInfo messageInfo1 = new MessageInfo();

        messageInfo1.setLen(123);
        messageInfo1.setOffset(1234567);
        list1.add(messageInfo1);
       // System.out.println(list1);


        MessageInfoQueue messageInfoQueue2 = (MessageInfoQueue) concurrentHashMap.get("TOPIC_03");
        messageInfoQueue2.setQueueId(3 + "");
        List list2 = messageInfoQueue2.getList();
        MessageInfo messageInfo2 = new MessageInfo();

        messageInfo2.setLen(123);
        messageInfo2.setOffset(1234567);
        list2.add(messageInfo2);
       // System.out.println(list2);


        MessageInfoQueue messageInfoQueue3 = (MessageInfoQueue) concurrentHashMap.get("TOPIC_00");
        messageInfoQueue3.setQueueId(0 + "");
        List list3 = messageInfoQueue3.getList();
        MessageInfo messageInfo3 = new MessageInfo();

        messageInfo3.setLen(123);
        messageInfo3.setOffset(1234567);
        list3.add(messageInfo3);
        /*System.out.println(((MessageInfoQueue) concurrentHashMap.get("3")).getList());

        System.out.println(messageInfoQueue);
        System.out.println(messageInfoQueue1);

        System.out.println(messageInfoQueue2);
        System.out.println(messageInfoQueue3);



        System.out.println(MessageInfoQueues.concurrentHashMap.hashCode());
*/
        NameServerInfo nameServerInfo = new NameServerInfo();
        nameServerInfo.setIp("127.0.0.1");
        nameServerInfo.setPort(8088);

        abstractStart.start(nameServerInfo);

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
