package io.openmessaging.net;

import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.producer.BrokerInfo;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class NettyServerTest {

    Logger logger = LoggerFactory.getLogger(NettyServerTest.class);
    private NettyServer nettyServer = null;

    @Before
    public void init(){
         nettyServer = new NettyServer();

    }


    @Test
    public  void testConn() throws InterruptedException {



        nettyServer.bind(ConstantNameServer.NAMESERVER_PORT);
        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setIp("127.0.0.1");
        brokerInfo.setPort(8080);


        HashMap topicQueueMap = new HashMap(1);
        topicQueueMap.put("TOPIC_01","10000");
        topicQueueMap.put("TOPIC_01","20000");
        topicQueueMap.put("TOPIC_01","30000");
        topicQueueMap.put("TOPIC_01","40000");
        topicQueueMap.put("TOPIC_01","50000");
        topicQueueMap.put("TOPIC_01","60000");
        topicQueueMap.put("TOPIC_01","70000");
        topicQueueMap.put("TOPIC_01","80000");
        topicQueueMap.put("TOPIC_01","90000");
        topicQueueMap.put("TOPIC_01","00000");






        //BrokerTopicTable.brokerTopicTable.put(brokerInfo,topicQueueMap);

        Thread.sleep(1000000);


     /*   Executor executor = Executors.newFixedThreadPool(1);
        logger.info("server were start");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                nettyServer.bind(8080);


            }
        };

        executor.execute(runnable);

        try {            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


    }



}