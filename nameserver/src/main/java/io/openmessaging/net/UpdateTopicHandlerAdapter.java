package io.openmessaging.net;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-10.
 */
public class UpdateTopicHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(UpdateTopicHandlerAdapter.class);

    private CountDownLatch countDownLatch = null;

    public UpdateTopicHandlerAdapter(CountDownLatch countDownLatch){

        this.countDownLatch = countDownLatch;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        System.out.println("notify over backed nameServer");




    }
}