package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.net.EncodeAndDecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-17.
 */
public class RestartHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(RestartHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();


}
