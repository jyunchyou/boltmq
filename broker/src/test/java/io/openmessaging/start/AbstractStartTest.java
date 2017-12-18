package io.openmessaging.start;

import io.openmessaging.broker.NameServerInfo;
import io.openmessaging.net.NettyServerTest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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





       // System.out.println(list);



       // System.out.println(list1);




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
