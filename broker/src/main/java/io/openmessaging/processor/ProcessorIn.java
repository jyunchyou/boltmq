package io.openmessaging.processor;

import io.netty.buffer.ByteBuf;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.store.MessageInfo;
import io.openmessaging.store.MessageInfoQueue;
import io.openmessaging.store.MessageInfoQueues;
import io.openmessaging.store.MessageStore;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorIn {



    private MessageStore messageStore = MessageStore.getMessageStore();

    private MessageInfoQueues messageInfoQueues = new MessageInfoQueues();

    //寻找queue
    public void input(ByteBuf byteBuf,String topic,String queueId){

        Set<Map.Entry> set = MessageInfoQueues.concurrentHashMap.entrySet();

        MessageInfoQueue messageInfoQueue = null;
        for (Map.Entry entry : set) {
            messageInfoQueue = (MessageInfoQueue) entry.getKey();
            String q = messageInfoQueue.getQueueId();

            if (q.equals(queueId)) {

                break;

            }


        }
        this.input(byteBuf,topic,messageInfoQueue);


    }

    //保存topic,设置index,判断是否需要new新文件
    public void input(ByteBuf byteBuf,String topic,MessageInfoQueue messageInfoQueue){

        List list = messageInfoQueue.getList();
        MessageInfo fileInfo = new MessageInfo();
        fileInfo.setTopic(topic);
        list.add(fileInfo);

        long index = messageInfoQueue.getMessageIndex();
        long previousIndex = messageInfoQueue.getPreviousMessageIndex();
        long byteBufLen = byteBuf.readableBytes();
        boolean newFile = false;

        long newPreviousIndex = previousIndex + ConstantBroker.FILE_SIZE;
        long newMessageIndex = index + byteBufLen;
        
        if (newMessageIndex > (newPreviousIndex)) {
            newFile = true;

            messageInfoQueue.setPreviousMessageIndex(newPreviousIndex);
        }
        messageInfoQueue.setMessageIndex(newMessageIndex);


        try {
            messageStore.input(byteBuf,newFile,messageInfoQueue.getQueueId(),previousIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }



}