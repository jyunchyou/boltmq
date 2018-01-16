package io.openmessaging.processor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.store.*;
import io.openmessaging.table.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorOut {




    private MessageStore messageStore = MessageStore.getMessageStore();


    public AbstractMessage out(String topic,int num,long uniqId){

        AbstractIndex abstractIndex = outIndex(topic,num,uniqId);
        return outMessage(topic,abstractIndex);


    }

    public AbstractIndex outIndex(String topic,int num,long uniqId) {
        IndexFileQueue indexFileQueue = IndexFileQueueMap.indexQueueMap.get(topic);
        for (int indexNum = 0; indexNum < num; indexNum++) {


            Long consumeIndex = ConsumerIndexTable.concurrentHashMap.get(uniqId);

            if (consumeIndex == null) {
                consumeIndex = 0l;
                ConsumerIndexTable.concurrentHashMap.put(uniqId,consumeIndex);

            }


            List list = indexFileQueue.getIndexFileQueue();

            long index = indexFileQueue.getIndex();
            while (consumeIndex >= index) {

                //TODO 等待新消息发送

                try {
                    Thread.sleep(ConstantBroker.WAIT_TIME_MESSAGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }




            int consumerQueueIndex = (int) (consumeIndex/ConstantBroker.INDEX_FILE_SIZE);
            AbstractIndexFile abstractIndexFile = (AbstractIndexFile) list.get(consumerQueueIndex);


            consumeIndex = consumeIndex - (consumerQueueIndex * ConstantBroker.INDEX_FILE_SIZE);
            AbstractIndex abstractIndex = abstractIndexFile.getIndex(Math.toIntExact(consumeIndex),(3 * 8));




            // messagesByte.add(messageByte);


            ConsumerIndexTable.concurrentHashMap.put(uniqId,consumeIndex + (3 * 8));


            return abstractIndex;


            // bufferSize += len;

        }

        //return this.converge(messagesByte,bufferSize);

        return null;

    }


    public AbstractMessage outMessage(String topic,AbstractIndex abstractIndex){

        long sendTime = abstractIndex.getSendTime();
        long index = abstractIndex.getIndex();
        long len = abstractIndex.getLen();


        FileQueue fileQueue = FileQueueMap.queueMap.get(topic);
        CopyOnWriteArrayList copyOnWriteArrayList = fileQueue.getFileQueue();
        //TODO 消息index类型统一

        int queueIndex = (int) (index/ConstantBroker.FILE_SIZE);
        AbstractFile abstractFile = (AbstractFile) copyOnWriteArrayList.get(queueIndex);

        long fileIndexLong = index - queueIndex * ConstantBroker.FILE_SIZE;

        int fileIndex = new Long(fileIndexLong).intValue();

        AbstractMessage abstractMessage = abstractFile.getMessage((int)fileIndex,(int)len);



        return abstractMessage;

    }
  /*  public ByteBuf out(String topic,int num,long uniqId){



            MessageInfoQueue messageInfoQueue = MessageInfoQueues.concurrentHashMap.get(topic);

            String queueId = messageInfoQueue.getQueueId();

            List<byte[]> messagesByte = new ArrayList();

            int bufferSize = 0;
//拉取num条
            for (int indexNum = 0;indexNum < num;indexNum++) {



                Long consumeIndex = ConsumerIndexTable.concurrentHashMap.get(uniqId);

                if (consumeIndex == null) {
                    ConsumerIndexTable.concurrentHashMap.put(uniqId,0l);
                    consumeIndex = 0l;
                }


                List list = messageInfoQueue.getList();

                while (consumeIndex >= list.size()) {

                    //TODO 等待新消息发送

                    try {
                        Thread.sleep(ConstantBroker.WAIT_TIME_MESSAGE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }



                long offset = messageInfo.getOffset();
                long len = messageInfo.getLen();

                byte[] messageByte = messageStore.out(offset,len,queueId);

                messagesByte.add(messageByte);

                ConsumerIndexTable.concurrentHashMap.put(uniqId);

                bufferSize += len;

            }

            return this.converge(messagesByte,bufferSize);
            }*/


            public ByteBuf converge(List<byte[]> messagesByte,int bufferSize){

        ByteBuf byteBuf = Unpooled.buffer(bufferSize);
        for (byte[] messageByte : messagesByte) {
            byteBuf.writeBytes(messageByte);
        }



        return byteBuf;
            }








}
