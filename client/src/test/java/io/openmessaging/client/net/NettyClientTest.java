package io.openmessaging.client.net;

import io.netty.channel.Channel;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.producer.NameServerInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyClientTest {

    private NettyClient nettyClient = null;

    private NameServerInfo nameServerInfo = null;

    private Logger logger = LoggerFactory.getLogger(NettyClientTest.class);

    @Before
    public void init(){
        nettyClient = NettyClient.getNettyClient();

        nameServerInfo = new NameServerInfo();

        nameServerInfo.setIp("127.0.0.1");

        nameServerInfo.setPort(8080);
    }

    @Test
    public void testConnServer() throws InterruptedException {
        Channel channel = nettyClient.bind(nameServerInfo);
        nettyClient.sendRouteRequest(channel);

        if (channel == null) {

            logger.info("得到Channel为null");

        }else{

            logger.info("成功得到channel");
        }
        Thread.sleep(300000);

    }
}
