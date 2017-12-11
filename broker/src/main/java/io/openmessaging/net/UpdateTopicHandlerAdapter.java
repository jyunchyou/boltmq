package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.store.MessageInfoQueue;
import io.openmessaging.store.MessageInfoQueues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-10.
 */
public class UpdateTopicHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(UpdateTopicHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;


        byte[] topicByteLen = new byte[1];
        byteBuf.readBytes(topicByteLen);
        int topicByteLenInt = topicByteLen[0];
        byte[] topicByte = new byte[topicByteLenInt];
        byteBuf.readBytes(topicByte);
        String topic = new String(topicByte);

        putTopicQueue(topic);

        ByteBuf back = Unpooled.buffer(1);
        back.writeBytes("0".getBytes());
        channelHandlerContext.writeAndFlush(back);
    }

    public void putTopicQueue(String topic){

        System.out.println("-----------notify update topic--------------");
        if (MessageInfoQueues.concurrentHashMap.containsKey(topic)){
            return ;
        }
        MessageInfoQueue messageInfoQueue = new MessageInfoQueue(topic);
        messageInfoQueue.setQueueId(topic);
        MessageInfoQueues.concurrentHashMap.put(topic,messageInfoQueue);

    }

}