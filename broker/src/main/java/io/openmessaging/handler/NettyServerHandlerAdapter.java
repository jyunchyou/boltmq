package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.processor.ProcessorIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServerHandlerAdapter extends SimpleChannelInboundHandler {

    Logger logger = LoggerFactory.getLogger(NettyServerHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private ProcessorIn processIn = new ProcessorIn();

    private Lock lock = null;

    private ByteBuf lastByteBuf = null;

    public NettyServerHandlerAdapter(Lock lock){

        this.lock = lock;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg){
        ByteBuf data = (ByteBuf) msg;
        if (lastByteBuf != null) {
            lastByteBuf.writeBytes(data);
            data = lastByteBuf;
        }
        lastByteBuf = encodeAndDecode.deCode(data,channelHandlerContext.channel(),processIn);


    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }



}
