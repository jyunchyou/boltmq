package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.processor.ProcessorIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-9.
 */
public class PullHandlerAdapter extends ChannelHandlerAdapter {


    Logger logger = LoggerFactory.getLogger(PullHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        byte[] pullRequest = new byte[byteBuf.readableBytes()];


        byteBuf.readBytes(pullRequest);

        System.out.println(new String(pullRequest)+"_______________________________________________________");
    }
}