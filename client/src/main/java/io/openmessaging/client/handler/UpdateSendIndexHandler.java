package io.openmessaging.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.client.net.EncodeAndDecode;
import io.openmessaging.client.producer.AbstractProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UpdateSendIndexHandler extends ChannelHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(UpdateSendIndexHandler.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private ByteBuf lastByteBuf = null;

    private AbstractProducer abstractProducer = null;

    private Channel channel = null;

    public UpdateSendIndexHandler(AbstractProducer abstractProducer, Channel channel){
        this.abstractProducer = abstractProducer;

        this.channel = channel;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed2");
    }

    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        logger.info("method channelActive has executed3");
        ByteBuf byteBuf = (ByteBuf) msg;


        if (lastByteBuf != null) {

            lastByteBuf.writeBytes(byteBuf);
            byteBuf = lastByteBuf;
        }


        lastByteBuf = encodeAndDecode.deCode(byteBuf,abstractProducer,channel);

    }


}