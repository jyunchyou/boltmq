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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
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

            ByteBuf byteBuf = encodeAndDecode.encodeSendList();


           /* byte[] d = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(d);
            System.out.println(new String(d));*/
            channelHandlerContext.writeAndFlush(byteBuf);




        } else {

            String topic = result;

            this.notifyAllBroker(topic);

            ByteBuf byteBuf = encodeAndDecode.encodeReceiveTable(result);

            if (byteBuf == null) {
                return ;
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


    public void notifyAllBroker(String topic){
    byte[] topicByte = topic.getBytes();

    byte topicByteLen = (byte) topicByte.length;
    //nitify all broker if that it has connected

    NettyServer nettyServer = NettyServer.getNettyServer();


    Set<BrokerInfo> set = BrokerInfoTable.map.keySet();

    ByteBuf byteBuf = Unpooled.buffer(topicByte.length + 1);

            byteBuf.writeBytes(new byte[]{topicByteLen});
            byteBuf.writeBytes(topicByte);
            for (BrokerInfo brokerInfo : set) {


                nettyServer.notifyBroker(brokerInfo, byteBuf,countDownLatch);
            }
    }


}
