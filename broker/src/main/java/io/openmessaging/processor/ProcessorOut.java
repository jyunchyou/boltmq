package io.openmessaging.processor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.store.MessageInfo;
import io.openmessaging.store.MessageInfoQueue;
import io.openmessaging.store.MessageInfoQueues;
import io.openmessaging.store.MessageStore;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorOut {




    private MessageStore messageStore = MessageStore.getMessageStore();

    public void out(String topic,int num){



            MessageInfoQueue messageInfoQueue = MessageInfoQueues.concurrentHashMap.get(topic);

            String queueId = messageInfoQueue.getQueueId();

            List<byte[]> messagesByte = new ArrayList();

            int bufferSize = 0;
//拉取num条
            for (int indexNum = 0;indexNum < num;indexNum++) {
                int consumeIndex = messageInfoQueue.getIndex();

                List list = messageInfoQueue.getList();

                if (consumeIndex >= list.size()) {

                    //TODO 等待新消息发送

                    try {
                        Thread.sleep(ConstantBroker.WAIT_TIME_MESSAGE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                MessageInfo messageInfo = (MessageInfo) list.get(consumeIndex);

                long offset = messageInfo.getOffset();
                long len = messageInfo.getLen();

                byte[] messageByte = messageStore.out(offset,len,queueId);

                messagesByte.add(messageByte);

                bufferSize += len;

            }

            this.pull(messagesByte,bufferSize);
            }


            public void pull(List<byte[]> messagesByte,int bufferSize){


        System.out.println(messagesByte);


            }








}
