package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.BrokerConnectionCacheTable;
import io.openmessaging.table.BrokerInfoTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        ByteBuf byteBuf = (ByteBuf) msg;
        ReferenceCountUtil.release(byteBuf);





    }


    //channel连接超时,在连接表中移除


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,Object object){


    //删连接
        BrokerConnectionCacheTable.concurrentHashMap.remove(brokerInfo);

    //删索引
        BrokerInfoTable.map.remove(brokerInfo);

        //TODO 保存消费下标




    }

}