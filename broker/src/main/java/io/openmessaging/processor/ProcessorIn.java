package io.openmessaging.processor;

import io.netty.buffer.ByteBuf;
import io.openmessaging.store.MessageInfo;
import io.openmessaging.store.MessageInfoQueue;
import io.openmessaging.store.MessageInfoQueues;
import io.openmessaging.store.MessageStore;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorIn {



    private MessageStore messageStore = MessageStore.getMessageStore();
    //寻找queue
    public void input(ByteBuf byteBuf,String topic,String queueId){

        Set<Map.Entry> set = MessageInfoQueues.concurrentHashMap.entrySet();

        MessageInfoQueue fileInfoQueue = null;
        for (Map.Entry entry : set) {
            fileInfoQueue = (MessageInfoQueue) entry.getKey();
            String q = fileInfoQueue.getQueueId();
            if (q.equals(queueId)) {

                break;

            }


        }
        this.input(byteBuf,topic,fileInfoQueue);


    }

    //保存topic
    public void input(ByteBuf byteBuf,String topic,MessageInfoQueue fileInfoQueue){

        List list = fileInfoQueue.getList();
        MessageInfo fileInfo = new MessageInfo();
        fileInfo.setTopic(topic);
        list.add(fileInfo);


    }

    //存储
    public void input(ByteBuf byteBuf,MessageInfo fileInfo){


        messageStore.input(byteBuf,fileInfo);


    }
}
