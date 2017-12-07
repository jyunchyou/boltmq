package io.openmessaging.consumer.net;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import io.openmessaging.consumer.table.ReceiveMessageTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;

/**
 * Created by fbhw on 17-12-2.
 */
public class UpdateFromNameServerHandler extends ChannelHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(UpdateFromNameServerHandler.class);

    private CountDownLatch countDownLatch = null;

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    public UpdateFromNameServerHandler(CountDownLatch countDownLatch){

        this.countDownLatch = countDownLatch;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg)  {



        ByteBuf byteBuf = (ByteBuf) msg;


        System.out.println("update success");

        encodeAndDecode.decodeReceiveTable(byteBuf);





        countDownLatch.countDown();
    }




}
