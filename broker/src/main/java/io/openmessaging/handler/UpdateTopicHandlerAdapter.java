package io.openmessaging.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.openmessaging.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-10.
 */
public class UpdateTopicHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(UpdateTopicHandlerAdapter.class);

    public void putTopicQueue(String topic){


        if (IndexFileQueueMap.indexQueueMap.containsKey(topic)){
            return ;
        }

        //设置索引map
        IndexFileQueue indexFileQueue = new IndexFileQueue(topic);

        IndexFileQueueMap.indexQueueMap.put(topic,indexFileQueue);

        //设置消息map
        //MessageFileQueue messageFileQueue = new MessageFileQueue(topic);

        //FileQueueMap.queueMap.put(topic, messageFileQueue);

    }

}