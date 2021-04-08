package io.openmessaging.consumer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.consumer.net.EncodeAndDecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UpdateSendIndexHandler extends ChannelHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(UpdateFromNameServerHandler.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg)  {

        logger.info("method channelActive has executed1");


        ByteBuf byteBuf = (ByteBuf) msg;

       // encodeAndDecode.decodeConfirmSend(byteBuf);


        ReferenceCountUtil.release(byteBuf);
    }


}