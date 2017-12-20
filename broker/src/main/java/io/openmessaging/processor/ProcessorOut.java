package io.openmessaging.processor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.store.*;
import io.openmessaging.table.ConsumerIndexTable;
import io.openmessaging.table.MessageInfo;
import io.openmessaging.table.MessageInfoQueue;
import io.openmessaging.table.MessageInfoQueues;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorOut {




    private MessageStore messageStore = MessageStore.getMessageStore();

    public ByteBuf out(String topic,int num,long uniqId){



            MessageInfoQueue messageInfoQueue = MessageInfoQueues.concurrentHashMap.get(topic);

            String queueId = messageInfoQueue.getQueueId();

            List<byte[]> messagesByte = new ArrayList();

            int bufferSize = 0;
//拉取num条
            for (int indexNum = 0;indexNum < num;indexNum++) {



                Integer consumeIndex = ConsumerIndexTable.concurrentHashMap.get(uniqId);

                if (consumeIndex == null) {
                    ConsumerIndexTable.concurrentHashMap.put(uniqId,0);
                    consumeIndex = 0;
                }


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

                ConsumerIndexTable.concurrentHashMap.put(uniqId,++indexNum);

                bufferSize += len;

            }

            return this.converge(messagesByte,bufferSize);
            }


            public ByteBuf converge(List<byte[]> messagesByte,int bufferSize){

        ByteBuf byteBuf = Unpooled.buffer(bufferSize);
        for (byte[] messageByte : messagesByte) {
            byteBuf.writeBytes(messageByte);
        }



        return byteBuf;
            }








}
