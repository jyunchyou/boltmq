package io.openmessaging.client.handler;

import com.aliyuncs.exceptions.ClientException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.client.net.SendResult;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.table.ConnectionCacheTable;
import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-11-25.
 */
public class NettyClientHandler extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    SendResult sendResult = null;

    private CountDownLatch countDownLatch = null;

    private BrokerInfo brokerInfo = null;

    public NettyClientHandler(SendResult sendResult, CountDownLatch countDownLatch,BrokerInfo brokerInfo){

        this.sendResult = sendResult;
        this.countDownLatch = countDownLatch;
        this.brokerInfo = brokerInfo;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){


        System.out.println("channelActivity has executing!");

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext,Object msg) throws ClientException {

        System.out.println("message back success");
        ByteBuf byteBuf  = (ByteBuf) msg;

        byte[] resultBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(resultBytes);

        String resultString = new String(resultBytes);




        if ("1".equals(resultString)) {
            System.out.println("send message back success!");

        }else{

            logger.warn("消息发送失败");
        }

        //同步发送释放
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }




}

//channel连接超时,在连接表中移除,锁释放
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx,Object object){

            Map map = ConnectionCacheTable.getConnectionCacheTable();

            map.remove(brokerInfo);
            for (SendQueue sendQueue : SendQueues.messageQueues){

                if (sendQueue.getBrokerInfo().equals(brokerInfo)) {
                    SendQueues.messageQueues.remove(sendQueue);

                }

            }
                countDownLatch.countDown();
        }


}
