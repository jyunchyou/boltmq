package io.openmessaging.consumer.net;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-9.
 */
public class ReceiveMessageHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ReceiveMessageHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {


    }
}