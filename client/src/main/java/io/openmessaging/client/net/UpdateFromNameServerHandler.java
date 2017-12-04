package io.openmessaging.client.net;

import com.aliyuncs.exceptions.ClientException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.openmessaging.client.producer.SendQueue;
import io.openmessaging.client.producer.SendQueues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by fbhw on 17-12-2.
 */
public class UpdateFromNameServerHandler extends ChannelHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(UpdateFromNameServerHandler.class);

    private CountDownLatch countDownLatch = null;

    private Condition condition = null;

    public UpdateFromNameServerHandler(CountDownLatch countDownLatch){

        this.countDownLatch = countDownLatch;

    }

    public UpdateFromNameServerHandler(Condition condition){
        this.condition = condition;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws ClientException {



        ByteBuf byteBuf = (ByteBuf) msg;

        byte[] resultByte = new byte[byteBuf.readableBytes()];

        byteBuf.readBytes(resultByte);
        logger.info("Server端返回消息:"+new String(resultByte));
/*

        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;
*/


        SendQueues.routeByteBuffer = ByteBuffer.allocate(resultByte.length);
        SendQueues.routeByteBuffer.put(resultByte);
        System.out.println("byteBuffer值为:"+new String((SendQueues.routeByteBuffer.array())));

        countDownLatch.countDown();
    }




}
