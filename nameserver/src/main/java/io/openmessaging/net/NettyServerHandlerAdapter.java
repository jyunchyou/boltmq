package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.BrokerTopicTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServerHandlerAdapter extends ChannelHandlerAdapter{

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    Logger logger = LoggerFactory.getLogger(NettyServerHandlerAdapter.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg){

        logger.info("method channelRead has executed");

        ByteBuf data = (ByteBuf) msg;

        byte[] b = new byte[data.readableBytes()];

        data.readBytes(b);

        String result = new String(b);

        logger.info("Server端收到消息:"+result);

        //test
        BrokerInfo brokerInfo = new BrokerInfo();

        brokerInfo.setIp("127.0.0.1");
        brokerInfo.setPort(8080);


        HashMap topicQueueMap = new HashMap(1);
        topicQueueMap.put("TOPIC_01","10000");
        BrokerTopicTable.brokerTopicTable.put(brokerInfo,topicQueueMap);



        ByteBuf byteBuf = encodeAndDecode.encodeSendList();

        System.out.println(byteBuf);


        channelHandlerContext.writeAndFlush(byteBuf);
/*

        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;
*/

    }

}
