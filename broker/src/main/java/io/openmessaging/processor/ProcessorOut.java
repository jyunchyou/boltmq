package io.openmessaging.processor;

import io.openmessaging.store.MessageInfo;
import io.openmessaging.store.MessageInfoQueue;
import io.openmessaging.store.MessageInfoQueues;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorOut {





    public void out(String topic,int num){


            int mapSize = MessageInfoQueues.concurrentHashMap.size();

            int randomNum = (int) Math.abs((Math.random() * 10)/mapSize);


            MessageInfoQueue messageInfoQueue = MessageInfoQueues.concurrentHashMap.get(randomNum);


            int consumeIndex = messageInfoQueue.getIndex();


            List list = messageInfoQueue.getList();


        if (consumeIndex >= list.size()) {

            //TODO 等待新消息发送
            return;
        }

            MessageInfo messageInfo = (MessageInfo) list.get(consumeIndex);

            while (!topic.equals(messageInfo.getTopic())) {

                messageInfoQueue.setIndex(++consumeIndex);


                if (consumeIndex >= list.size()) {

                    //TODO 等待新消息发送
                    return;
                }
                messageInfo = (MessageInfo) list.get(consumeIndex);

            }

        }

    }



}
