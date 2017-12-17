package io.openmessaging.net;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.BrokerConnectionCacheTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-10.
 */
public class UpdateTopicHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(UpdateTopicHandlerAdapter.class);

    private CountDownLatch countDownLatch = null;

    private BrokerInfo brokerInfo = null;

    public UpdateTopicHandlerAdapter(CountDownLatch countDownLatch,BrokerInfo brokerInfo){

        this.countDownLatch = countDownLatch;
        this.brokerInfo = brokerInfo;

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        System.out.println("notify over backed nameServer");




    }


    //channel连接超时,在连接表中移除


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,Object object){

        Map map = BrokerConnectionCacheTable.concurrentHashMap;

        map.remove(brokerInfo);

    //TODO  是先删索引,先保存索引,还是超时broker的索引不变

    }

}