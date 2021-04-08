
package io.openmessaging.processor;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.start.AbstractStart;
import io.openmessaging.store.MessageStore;
import io.openmessaging.table.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorIn {

    private MessageStore messageStore = MessageStore.getMessageStore();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private int sum = 0;

    public void inputMessage(byte[] byteBuf, String topic) {

        long writeIndex = MessageFileQueue.fileWriteIndex;

        long fileNameLong = (MessageFileQueue.queueIndex * ConstantBroker.FILE_SIZE);
        long newFileNameLong = (fileNameLong + ConstantBroker.FILE_SIZE);
        AbstractMessageFile abstractMessageFile = null;
        if (newFileNameLong < (writeIndex + byteBuf.length)) {
            abstractMessageFile = new AbstractMessageFile(String.valueOf(newFileNameLong), (int) ConstantBroker.FILE_SIZE);
            MessageFileQueue.abstractMessageFile = abstractMessageFile;
            MessageFileQueue.queueIndex = (MessageFileQueue.queueIndex + 1);
            MessageFileQueue.fileWriteIndex = (newFileNameLong);
            writeIndex = newFileNameLong;
             } else {
            //queue 0下标文件的初始化
            if (MessageFileQueue.abstractMessageFile == null) {
                abstractMessageFile = new AbstractMessageFile(0 + "", (int) ConstantBroker.FILE_SIZE);
                MessageFileQueue.abstractMessageFile = (abstractMessageFile);

            } else {
                abstractMessageFile = MessageFileQueue.abstractMessageFile;
            }
        }
        //设置下一个消息存放地址
        MessageFileQueue.fileWriteIndex = (MessageFileQueue.fileWriteIndex + byteBuf.length);
        //写入新增索引
        inputIndex(writeIndex, byteBuf.length, System.currentTimeMillis(), topic);

        abstractMessageFile.putMessage(byteBuf);


        System.out.println(sum++);

    }


    //写入索引，一个索引节点包含消息下标，消息长度，发送时间
    public void inputIndex(long offset, int len, long sendTime, String topic) {
        //索引编码成字节数组

        //从map中获取topic下索引文件队列
        IndexFileQueue indexFileQueue = IndexFileQueueMap.indexQueueMap.get(topic);


        if (indexFileQueue == null) {
            indexFileQueue = new IndexFileQueue(topic);
            IndexFileQueueMap.indexQueueMap.put(topic, indexFileQueue);
        }

        long index = indexFileQueue.getIndex();
        indexFileQueue.setMessageIndex(indexFileQueue.getMessageIndex() + 1);
        //计算当前已用文件大小
        long fileNameLong = (indexFileQueue.getQueueIndex() * ConstantBroker.INDEX_FILE_SIZE);
        //计算下一个文件用到总的地址大小
        long newFileNameLong = (fileNameLong + ConstantBroker.INDEX_FILE_SIZE);

        //如果超过文件限制大小，增加新文件
        AbstractIndexFile abstractIndexFile = null;

        if (newFileNameLong < (index + ConstantBroker.INDEX_SIZE)) {
            //新增node
            abstractIndexFile = new AbstractIndexFile(topic, new String(String.valueOf(newFileNameLong)), (int) ConstantBroker.INDEX_FILE_SIZE);
            indexFileQueue.setAbstractIndexFile(abstractIndexFile);
            //文件数量下标+1
            indexFileQueue.setQueueIndex(indexFileQueue.getQueueIndex() + 1);
            indexFileQueue.setIndex(newFileNameLong + ConstantBroker.INDEX_SIZE);
        } else {
            //queue 0下标文件的初始化
            if (indexFileQueue.getAbstractIndexFile() == null) {
                abstractIndexFile = new AbstractIndexFile(topic, 0 + "", (int) ConstantBroker.INDEX_FILE_SIZE);
                indexFileQueue.setAbstractIndexFile(abstractIndexFile);
            } else {
                abstractIndexFile = indexFileQueue.getAbstractIndexFile();
            }
            indexFileQueue.setIndex(index + ConstantBroker.INDEX_SIZE);

        }
        //设置下一个索引写入下标
        //写入索引
        abstractIndexFile.putIndex(offset, len, sendTime);


    }

    //寻找queue
    public void input(byte[] byteBuf, String topic, long sendTime) {
        MessageInfoQueue messageInfoQueue = MessageInfoQueues.concurrentHashMap.get(topic);


        this.input(byteBuf, topic, messageInfoQueue, sendTime);


    }

    //保存topic,设置index,判断是否需要new新文件
    public void input(byte[] byteBuf, String topic, MessageInfoQueue messageInfoQueue, long sendTime) {


        List list = messageInfoQueue.getList();

        List<MessageInfo> l = messageInfoQueue.getList();

        long index;
        if (l.size() == 0) {
            index = 0;
        } else {
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

        if ((newMessageIndex + byteBuf.length) > (newPreviousIndex)) {
            newFile = true;
            messageInfoQueue.setPreviousMessageIndex((newMessageIndex + byteBuf.length));
        }
        try {
            messageStore.input(byteBuf, newFile, topic, previousIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {


/*

        RandomAccessFile randomAccessFile = new RandomAccessFile(new File("D:\\cjnf\\topictest1\\250"),"rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0,1073741824);

        System.out.println(mappedByteBuffer.getLong());

        System.out.println(mappedByteBuffer.getLong());
        System.out.println(mappedByteBuffer.getLong());
*/



        AbstractStart.loadIndexMap();
        AbstractStart.loadMassageIndex();


        ProcessorIn processorIn = new ProcessorIn();
        for (int index = 0; index < 20; index++) {
            processorIn.inputMessage(("1234567890" + index).getBytes(), "teamWang");

        }

        ProcessorOut processorOut = new ProcessorOut();
        for (int index = 1;index < 20;index++) {



            processorOut.outIndexShardingPage("teamWang", index, 1, null,false,null);

        }

    }
}