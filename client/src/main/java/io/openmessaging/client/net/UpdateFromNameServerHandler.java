package io.openmessaging.client.net;

import com.aliyuncs.exceptions.ClientException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.client.table.SendQueues;
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


        System.out.println("update success");


//        logger.info("Server端返回消息:"+ new String(bb.array()));

/*

        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;
*/


        SendQueues.routeByteBuf = byteBuf;




        countDownLatch.countDown();
    }




}
