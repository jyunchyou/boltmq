package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import io.openmessaging.table.TopicBrokerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-7.
 */
public class BrokerTableHandlerAdapter extends ChannelHandlerAdapter {



    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    Logger logger = LoggerFactory.getLogger(BrokerTableHandlerAdapter.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("activity");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;
        encodeAndDecode.decode(byteBuf);
        System.out.println(TopicBrokerTable.concurrentHashMap.get("TOPIC_01"));


    }
    }

