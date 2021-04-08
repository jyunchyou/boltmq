package io.openmessaging.processor;

import com.google.common.primitives.Longs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ConstantPool;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.store.*;
import io.openmessaging.table.*;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by fbhw on 17-12-5.
 */
public class ProcessorOut {

    private MessageStore messageStore = MessageStore.getMessageStore();

    private ConsumeIndexFileMap consumeIndexFileMap = new ConsumeIndexFileMap();

    private ConsumeMessageFileMap consumeMessageFileMap = new ConsumeMessageFileMap();

    private int sum = 0;
    public static void main(String[] args) {

    }
    public boolean consumeWait(String topic,long messageOffset,long num){



        int count = 0;
        while (count < ConstantBroker.RE_TRY_CONSUME_COUNT) {
            IndexFileQueue indexFileQueue = IndexFileQueueMap.indexQueueMap.get(topic);
            if (indexFileQueue.getMessageIndex() < messageOffset + num) {
                //生产消息不够，挂起一段时间重试，直到超过一定次数返回给消费端，消费端隔段时间重新发起请求
                try {
                    Thread.sleep(ConstantBroker.TE_TRY_CONSUME_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //count++;
                continue;
            }else{
                return true;
            }
        }
        return false;
    }

    //最多使用到两个索引文件,消费端对一次性消费数量做限制
    public AbstractIndex outIndexShardingPage(String topic, long messageOffset, long num, ChannelHandlerContext cxt, boolean consumeModel, Map ackMap) throws IOException, ExecutionException, InterruptedException {

        System.out.println(topic +"_" +messageOffset + "_" + num);
        if (!consumeWait(topic,messageOffset,num)) {
            //消费未有最新数据，返回消费端重新消费
            return null;
        }

        //计算索引在第几个文件

        long fileNum = (messageOffset - 1)/ConstantBroker.INDEX_FILE_NODE_NUM;
        String fileName = fileNum * ConstantBroker.INDEX_FILE_SIZE + "";
        //页中读取位置
        System.out.println(fileName + "@@@@");
        int readOffsetNum = (int) ((messageOffset - 1)%ConstantBroker.INDEX_FILE_NODE_NUM);
        int readOffset = readOffsetNum * ConstantBroker.INDEX_SIZE;
        //计算是否需要换页
        System.out.println(readOffset);
        if (readOffsetNum + num > ConstantBroker.INDEX_FILE_NODE_NUM) {
            //超过一页;
            int firstPageNodeNum = ConstantBroker.INDEX_FILE_NODE_NUM - readOffsetNum;
            outIndex(topic,fileName,readOffset,firstPageNodeNum,cxt,messageOffset,consumeModel,ackMap);//单页下标
            //换文件
            fileName = (fileNum + 1) * ConstantBroker.INDEX_FILE_SIZE + "";
            outIndex(topic,fileName,0,(int)num - firstPageNodeNum,cxt,messageOffset,consumeModel,ackMap);//第二页下标
        }else{
            outIndex(topic,fileName,readOffset,(int)num,cxt,messageOffset,consumeModel,ackMap);//单页下标
        }
        return null;
    }

    public void outIndex(String topic,String fileName,int index,int num,ChannelHandlerContext cxt,long messageOffset,boolean concumeModel,Map ackMap) throws IOException, ExecutionException, InterruptedException {

        MappedByteBuffer mappedByteBuffer = consumeIndexFileMap.getMappedByteBuffer(topic,fileName);
        mappedByteBuffer.position(index);

        for (int i = 0;i < num;i++) {
            long messageIndex = mappedByteBuffer.getLong();
            int messageLen = mappedByteBuffer.getInt();
            long sendTime = mappedByteBuffer.getLong();
            mappedByteBuffer.get();
            outMassage(messageIndex,messageLen,sendTime,cxt,messageOffset,concumeModel,ackMap);
        }
    }

    public void outMassage(long messageIndex,int messageLen,long sendTime,ChannelHandlerContext cxt,long messageOffset,boolean consumeModel,Map ackMap) throws IOException, ExecutionException, InterruptedException {

        //文件内的开始消费地址


        int fileIndex = (int) (messageIndex%ConstantBroker.FILE_SIZE);

        System.out.println(fileIndex + "%");
        String fileName = messageIndex/ConstantBroker.FILE_SIZE * ConstantBroker.FILE_SIZE + "";



        System.out.println(fileName + "#");
        MappedByteBuffer mappedByteBuffer = consumeMessageFileMap.getMappedByteBuffer(fileName);

        mappedByteBuffer.position(fileIndex);

        byte[] message = new byte[messageLen];

        mappedByteBuffer.get(message);


        ByteBuf byteBuf = Unpooled.buffer(messageLen);

//-------------------------------------------------------------------------------
      /*  byteBuf.writeBytes(message);

        int totalLen = byteBuf.readInt();

        System.out.println("totalLen" + totalLen);
        int readable = byteBuf.readableBytes();

        System.out.println("*****"+ byteBuf.readerIndex());
        //消息类型
        byteBuf.skipBytes(19);//跳过一定字节数读取topic

        System.out.println("*****"+ byteBuf.readerIndex());

        byte topicLenByte = byteBuf.readByte();

        System.out.println("*****"+ byteBuf.readerIndex());
        int topicLen = topicLenByte;
        System.out.println(topicLen);
        byte[] topicByte = new byte[topicLen];
        byteBuf.readBytes(topicByte);
        String topic = new String(topicByte);
        int valueLen = byteBuf.readInt();
        byte[] value = new byte[valueLen];
        byteBuf.readBytes(value);
        System.out.println(topic + new String(value));
*/
        //设置消费id
        byte[] consumeOffsetBytes = Longs.toByteArray(messageOffset);
        System.arraycopy(consumeOffsetBytes,0,message,messageLen - 16,consumeOffsetBytes.length);
        ChannelFuture channelFuture = cxt.write(byteBuf.writeBytes(message));
        cxt.flush();
        channelFuture.sync();

        if (consumeModel) {
            ackMap.put(messageOffset,null);
        }

        System.out.println(sum++);
        if (ConstantBroker.CONSUME_PROMPTLE) {
            cxt.flush();


             }
    }

/*
    public AbstractIndex outIndex(String topic,int massageOffset,int num,long uniqId) {
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

    }*/


    public AbstractMessage outMessage(String topic,AbstractIndex abstractIndex){

        long sendTime = abstractIndex.getSendTime();
        long index = abstractIndex.getIndex();
        long len = abstractIndex.getLen();


        MessageFileQueue messageFileQueue = FileQueueMap.queueMap.get(topic);
        CopyOnWriteArrayList copyOnWriteArrayList /*= messageFileQueue.getFileQueue()*/ = null;
        //TODO 消息index类型统一

        int queueIndex = (int) (index/ConstantBroker.FILE_SIZE);
        AbstractMessageFile abstractMessageFile = (AbstractMessageFile) copyOnWriteArrayList.get(queueIndex);

        long fileIndexLong = index - queueIndex * ConstantBroker.FILE_SIZE;

        int fileIndex = new Long(fileIndexLong).intValue();

        AbstractMessage abstractMessage = abstractMessageFile.getMessage((int)fileIndex,(int)len);



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
