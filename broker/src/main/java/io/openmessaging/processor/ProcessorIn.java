
package io.openmessaging.processor;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.store.MessageStore;
import io.openmessaging.table.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorIn {



    private MessageStore messageStore = MessageStore.getMessageStore();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();



    public void inputMessage(byte[] byteBuf,String topic,String queueId,long sendTime){


        for (int i= 0;i < byteBuf.length;i++) {
            System.out.print(byteBuf[i]);
        }
        System.out.println();
        FileQueue fileQueue = FileQueueMap.queueMap.get(queueId);
        long writeIndex = fileQueue.getFileWriteIndex();



        long fileNameLong = (fileQueue.getQueueIndex() * ConstantBroker.FILE_SIZE);


        long newFileNameLong = (fileNameLong + ConstantBroker.FILE_SIZE);


        AbstractFile abstractFile = null;
        if (newFileNameLong < (writeIndex + byteBuf.length)) {

            abstractFile = new AbstractFile(queueId,new String(String.valueOf(newFileNameLong)), (int) ConstantBroker.FILE_SIZE);


            fileQueue.getFileQueue().add(abstractFile);
            fileQueue.setQueueIndex(fileQueue.getQueueIndex() + 1);
        }else {

            //queue 0下标文件的初始化
            if (fileQueue.getFileQueue().size() <= 0) {
                abstractFile = new AbstractFile(queueId,0 + "", (int) ConstantBroker.FILE_SIZE);

                fileQueue.getFileQueue().add(abstractFile);


            }else {
                abstractFile = (AbstractFile) fileQueue.getFileQueue().get(fileQueue.getQueueIndex());
            }

        }

        long offset = 0;
        if (writeIndex != 0) {

            offset = writeIndex + byteBuf.length;
        }
        //写入新增索引
        inputIndex(offset,byteBuf.length,sendTime,queueId);
        fileQueue.setFileWriteIndex(offset);

        System.out.println("input message Len:"+byteBuf.length);

        abstractFile.putMessage(byteBuf);


    }

    //offset len sendTime
    public void inputIndex(long offset,long len,long sendTime,String queueId){


        System.out.println("input消息下标:"+offset);
        byte[] indexByte = encodeAndDecode.encodeIndex(offset,len,sendTime);

        IndexFileQueue indexFileQueue = IndexFileQueueMap.indexQueueMap.get(queueId);

        long index = indexFileQueue.getIndex();

        long fileNameLong = (indexFileQueue.getQueueIndex() * ConstantBroker.INDEX_FILE_SIZE);



        long newFileNameLong = (fileNameLong + ConstantBroker.INDEX_FILE_SIZE);
        //如果超过文件限制大小，增加新文件

        AbstractIndexFile abstractIndexFile = null;
        if (newFileNameLong < (index + len)) {


            abstractIndexFile.flip();
            abstractIndexFile = new AbstractIndexFile(queueId,new String(String.valueOf(newFileNameLong)), (int) ConstantBroker.INDEX_FILE_SIZE);

            indexFileQueue.getIndexFileQueue().add(abstractIndexFile);
            indexFileQueue.setQueueIndex(indexFileQueue.getQueueIndex() + 1);
        }else {
            //queue 0下标文件的初始化
            if (indexFileQueue.getIndexFileQueue().size() <= 0) {
                abstractIndexFile = new AbstractIndexFile(queueId,0 + "", (int) ConstantBroker.FILE_SIZE);

                indexFileQueue.getIndexFileQueue().add(abstractIndexFile);


            }else {
                abstractIndexFile =  indexFileQueue.getIndexFileQueue().get(indexFileQueue.getQueueIndex());
            }
        }

        indexFileQueue.setIndex(index + (3 * 8));
        abstractIndexFile.putIndex(indexByte);

    }
    //寻找queue
    public void input(byte[] byteBuf,String topic,String queueId,long sendTime){




        MessageInfoQueue messageInfoQueue = MessageInfoQueues.concurrentHashMap.get(topic);



        this.input(byteBuf,topic,queueId,messageInfoQueue,sendTime);


    }

    //保存topic,设置index,判断是否需要new新文件
    public void input(byte[] byteBuf,String topic,String queueId,MessageInfoQueue messageInfoQueue,long sendTime){


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

        m.setSendTime(sendTime);
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
