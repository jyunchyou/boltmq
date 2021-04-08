package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.processor.ProcessorOut;
import io.openmessaging.table.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Future;

public class UpdateConsumeIndexHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(io.openmessaging.handler.PullHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private ProcessorOut processorOut = new ProcessorOut();

    private ByteBuf lastByteBuf = null;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        //encodeAndDecode.deCode(byteBuf,ctx.channel());

        if (lastByteBuf != null) {
            lastByteBuf.writeBytes(byteBuf);
            byteBuf = lastByteBuf;
        }
        //lastByteBuf = EncodeAndDecode.decodeConsumeBackge(byteBuf, processorOut, ctx,);


    }
}

