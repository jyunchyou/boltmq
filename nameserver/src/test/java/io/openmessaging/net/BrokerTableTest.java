package io.openmessaging.net;

import io.openmessaging.constant.ConstantNameServer;
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
        nettyServer = NettyServer.getNettyServer();

    }

    @Test
    public void testConn(){

        nettyServer.bindBroker(ConstantNameServer.RECEIVE_FROM_BROKER_PORT);



        nettyServer.bind(8090);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




       /* BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setIp("127.0.0.1");
        brokerInfo.setPort(8088);
        MessageInfoQueues messageInfoQueues = (MessageInfoQueues) BrokerInfoTable.map.get(brokerInfo);


        MessageInfoQueue messageInfoQueue = (MessageInfoQueue) messageInfoQueues.getConcurrentHashMap().get("1");


        MessageInfo messageInfo = (MessageInfo) messageInfoQueue.getList().get(0);

*/

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
