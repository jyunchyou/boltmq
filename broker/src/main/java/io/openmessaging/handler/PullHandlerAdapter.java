package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.processor.ProcessorOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by fbhw on 17-12-9.
 */
public class PullHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(PullHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private ProcessorOut processorOut = new ProcessorOut();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        Map map = encodeAndDecode.decodePull(byteBuf);

        if (map == null) {

            channelHandlerContext.writeAndFlush(Unpooled.buffer(0));
            return;
        }
        String topic = (String) map.get("topic");
        int pullNum = (int) map.get("pullNum");
        long uniqId = (long) map.get("uniqId");


        ByteBuf backByteBuf = processorOut.out(topic,pullNum,uniqId);

        Future pullBackFuture = channelHandlerContext.writeAndFlush(backByteBuf);


        if (pullBackFuture.isSuccess()) {

            logger.info("pull message back success");
        }else {
            logger.info("pull message back fail");
        }


    }
}