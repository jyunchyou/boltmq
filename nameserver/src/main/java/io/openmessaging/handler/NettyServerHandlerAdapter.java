package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.net.NettyServer;
import io.openmessaging.producer.BrokerInfo;

import io.openmessaging.table.BrokerInfoTable;
import io.openmessaging.table.BrokerTopicTable;
import io.openmessaging.table.TopicBrokerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServerHandlerAdapter extends ChannelHandlerAdapter{

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    CountDownLatch countDownLatch = new CountDownLatch(1);

    Logger logger = LoggerFactory.getLogger(NettyServerHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg){


        ByteBuf data = (ByteBuf) msg;

        byte[] b = new byte[data.readableBytes()];

        data.readBytes(b);

        String result = new String(b);


        //test
        if ("getList".equals(result)) {

            while (TopicBrokerTable.concurrentHashMap.size() <= 0) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            ByteBuf byteBuf = encodeAndDecode.encodeSendList();


            //Test
            byteBuf.markReaderIndex();
            byte[] d = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(d);
            System.out.println("准备返回getLIst"+new String(d));
            byteBuf.resetReaderIndex();

            channelHandlerContext.writeAndFlush(byteBuf);




        } else {

            String topic = result;


            System.out.println(topic);
            this.notifyAllBroker(topic);

            ByteBuf byteBuf = null;

            while ((byteBuf = encodeAndDecode.encodeReceiveTable(result)) == null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(byteBuf);


            if (channelFuture.isSuccess()) {

            }else {

            }
        }
/*

        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;
*/

    }




    //TODO !

    public void notifyAllBroker(String topic) {

        byte[] topicByte = topic.getBytes();

        byte topicByteLen = (byte) topicByte.length;
        //nitify all broker if that it has connected

        NettyServer nettyServer = NettyServer.getNettyServer();


        Set<BrokerInfo> set = BrokerTopicTable.concurrentHashMap.keySet();
        for (BrokerInfo brokerInfo : set) {
            ByteBuf byteBuf = Unpooled.buffer(topicByte.length + 1);

            byteBuf.writeBytes(new byte[]{topicByteLen});
            byteBuf.writeBytes(topicByte);
            nettyServer.notifyBroker(brokerInfo, byteBuf, countDownLatch);


        }

    }

}
