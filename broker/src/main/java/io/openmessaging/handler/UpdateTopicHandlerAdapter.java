package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

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

        ReferenceCountUtil.release(byteBuf);



    }

    public void putTopicQueue(String topic){


        if (IndexFileQueueMap.indexQueueMap.containsKey(topic)){
            return ;
        }

        //设置索引map
        IndexFileQueue indexFileQueue = new IndexFileQueue(topic);

        IndexFileQueueMap.indexQueueMap.put(topic,indexFileQueue);

        //设置消息map
        FileQueue fileQueue = new FileQueue(topic);

        FileQueueMap.queueMap.put(topic,fileQueue);

    }

}