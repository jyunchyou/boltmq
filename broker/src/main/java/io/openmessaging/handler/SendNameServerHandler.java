package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-7.
 */
public class SendNameServerHandler extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(SendNameServerHandler.class);

}