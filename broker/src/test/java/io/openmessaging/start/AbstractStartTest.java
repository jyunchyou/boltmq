package io.openmessaging.start;

import io.openmessaging.exception.RegisterException;
import io.openmessaging.net.NettyServerTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractStartTest {

    Logger logger = LoggerFactory.getLogger(NettyServerTest.class);


    /*@Before
    public void init(){
        abstractStart = new AbstractStart();


    }*/


    @Test
    public void testStart() throws RegisterException {

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
        BProperties BProperties = new BProperties();
        BProperties.setIp("127.0.0.1");
        BProperties.setPort(7790);//服务器的
        BProperties.setGroup("brokers");
        BProperties.setServer("127.0.0.1:8848");
        BProperties.setServiceName("broker");

        AbstractStart.registry(BProperties);

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
