package io.openmessaging.client.net;

import com.aliyuncs.exceptions.ClientException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.client.producer.SendQueues;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-2.
 */
public class UpdateFromNameServerHandler extends ChannelHandlerAdapter {

    private CountDownLatch countDownLatch = null;

    public UpdateFromNameServerHandler(CountDownLatch countDownLatch){

        this.countDownLatch = countDownLatch;

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws ClientException {


        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;

        countDownLatch.notify();
    }
}
