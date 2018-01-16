package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.start.BrokerRestart;
import io.openmessaging.store.IndexStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by fbhw on 17-12-7.
 */
public class BrokerTableHandlerAdapter extends ChannelHandlerAdapter {

    private IndexStore indexStore = IndexStore.getIndexStore();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    Logger logger = LoggerFactory.getLogger(BrokerTableHandlerAdapter.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("activity");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;


        byteBuf.markReaderIndex();
        byte[] test = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(test);
        byteBuf.resetReaderIndex();

        byteBuf.markReaderIndex();
        String result = encodeAndDecode.decode(byteBuf);
        indexStore.saveOrRestart(byteBuf,result,channelHandlerContext);

    }
    }

