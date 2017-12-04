package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServerHandlerAdapter extends ChannelHandlerAdapter{

    Logger logger = LoggerFactory.getLogger(NettyServerHandlerAdapter.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg){

        logger.info("method channelRead has executed");

        ByteBuf data = (ByteBuf) msg;

        byte[] b = new byte[data.readableBytes()];

        data.readBytes(b);

        String result = new String(b);

        logger.info("Server端收到消息:"+result);

        channelHandlerContext.writeAndFlush(Unpooled.wrappedBuffer("fsdfa".getBytes()));
/*

        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;
*/

    }

}
