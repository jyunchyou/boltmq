package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-17.
 */
public class RestartHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(RestartHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;
        //TODO 解析为Index

        encodeAndDecode.decodeRestart(byteBuf);



    }

}
