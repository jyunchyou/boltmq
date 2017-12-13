package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.openmessaging.processor.ProcessorIn;
import io.openmessaging.processor.ProcessorOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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



        System.out.println("准备读取...");

        byte[] topicByteLen = new byte[1];
        byteBuf.readBytes(topicByteLen);
        int topicByteLenInt = topicByteLen[0];
        byte[] topicByte = new byte[topicByteLenInt];
        byteBuf.readBytes(topicByte);

        String topic = new String(topicByte);
        byte[] pullNumByte = new byte[1];
        byteBuf.readBytes(pullNumByte);

        int pullNum = pullNumByte[0];

        long uniqId = byteBuf.readLong();

        ByteBuf backByteBuf = processorOut.out(topic,pullNum,uniqId);

        Future pullBackFuture = channelHandlerContext.writeAndFlush(backByteBuf);

        if (pullBackFuture.isSuccess()) {

            logger.info("pull message back success");
        }else {
            logger.info("pull message back fail");
        }


    }
}