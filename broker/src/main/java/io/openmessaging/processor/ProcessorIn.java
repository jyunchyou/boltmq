
package io.openmessaging.processor;

import io.netty.buffer.ByteBuf;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.broker.BrokerInfo;
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



    //寻找queue
    public void input(byte[] byteBuf,String topic,String queueId){



        MessageInfoQueue messageInfoQueue = MessageInfoQueues.concurrentHashMap.get(topic);



        this.input(byteBuf,topic,queueId,messageInfoQueue);


    }

    //保存topic,设置index,判断是否需要new新文件
    public void input(byte[] byteBuf,String topic,String queueId,MessageInfoQueue messageInfoQueue){


        List list = messageInfoQueue.getList();

        List<MessageInfo> l = messageInfoQueue.getList();

        long index;
        if (l.size() == 0) {
            index = 0;
        }else {
            MessageInfo lastMessageInfo = l.get((l.size() - 1));
            index = lastMessageInfo.getOffset() + lastMessageInfo.getLen();
        }


        long previousIndex = messageInfoQueue.getPreviousMessageIndex();
        long byteBufLen = byteBuf.length;
        boolean newFile = false;

        long newPreviousIndex = previousIndex + ConstantBroker.FILE_SIZE;
        long newMessageIndex = index;


        MessageInfo m = new MessageInfo();
        m.setTopic(topic);
        m.setOffset(index);
        m.setLen(byteBuf.length);

        list.add(m);

        if ((newMessageIndex + byteBuf.length)> (newPreviousIndex)) {
            newFile = true;

            messageInfoQueue.setPreviousMessageIndex((newMessageIndex + byteBuf.length));
        }




        try {
            messageStore.input(byteBuf,newFile,queueId,previousIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }



}
