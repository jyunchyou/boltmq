package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.BrokerTopicTable;
import io.openmessaging.table.MessageInfo;
import io.openmessaging.table.MessageInfoQueue;
import io.openmessaging.table.MessageInfoQueues;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by fbhw on 17-12-4.
 */
public class EncodeAndDecode {

    private MessageInfoQueues messageInfoQueues = new MessageInfoQueues();

    /**
     *nameServerRouteTable协议格式
     *
     *  topicName长度 1字节
     *  topicName byte[]
     queueId长度 1字节
     queueId byte[]
     brokerInfo {
     ip长度 1字节
     ip byte[]
     port转String长度 1字节
     port 转为byte[]
     }
     */
    public ByteBuf encodeSendList() {



        ByteBuf heapBuffer = Unpooled.buffer(ConstantNameServer.ROUTE_TABLE_BUFFER_SIZE);



        Set<Map.Entry<BrokerInfo, HashMap<String, String>>> set = BrokerTopicTable.brokerTopicTable.entrySet();

        for (Map.Entry e : set) {


            Map<String, String> m = (Map<String, String>) e.getValue();
            Set<Map.Entry<String, String>> topicQueueSet = m.entrySet();
            for (Map.Entry topicQueueEntry : topicQueueSet) {
                BrokerInfo brokerInfo = (BrokerInfo) e.getKey();
                String topicName = (String) topicQueueEntry.getKey();
                String queueId = (String) topicQueueEntry.getValue();
                String ip = brokerInfo.getIp();
                String port = String.valueOf(brokerInfo.getPort());
                byte[] topicNameByte = topicName.getBytes();
                byte[] queueIdByte = queueId.getBytes();
                byte[] ipByte = ip.getBytes();
                byte[] portByte = port.getBytes();

                byte topicNameByteLen = (byte) topicNameByte.length;
                byte queueIdByteLen = (byte) queueIdByte.length;
                byte ipByteLen = (byte) ipByte.length;
                byte portByteLen = (byte) portByte.length;


                heapBuffer.writeBytes(new byte[]{topicNameByteLen});
                heapBuffer.writeBytes(topicNameByte);
                heapBuffer.writeBytes(new byte[]{queueIdByteLen});
                heapBuffer.writeBytes(queueIdByte);
                heapBuffer.writeBytes(new byte[]{ipByteLen});
                heapBuffer.writeBytes(ipByte);
                heapBuffer.writeBytes(new byte[]{portByteLen});
                heapBuffer.writeBytes(portByte);




            }
                /*ip长度 1字节
                 ip byte[]
                 port转String长度 1字节
                 port 转为byte[]
                 */
        }
        return heapBuffer;


    }


    //brokerInfo-map{Ｎ*(topicName-queueId)

    /**
     *ReceiveTable协议格式
     *
     *  topicName长度 1字节
     *  topicName byte[]
     queueId长度 1字节
     queueId byte[]
     brokerInfo {
     ip长度 1字节
     ip byte[]
     port转String长度 1字节
     port 转为byte[]
     }
     */
    public ByteBuf encodeReceiveTable(String topic){

        ByteBuf heapBuffer = Unpooled.buffer(ConstantNameServer.ROUTE_TABLE_BUFFER_SIZE);



        Set<Map.Entry<BrokerInfo, HashMap<String, String>>> set = BrokerTopicTable.brokerTopicTable.entrySet();

        for (Map.Entry brokerEntry : set) {
            Map map = (Map) brokerEntry.getValue();

           Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {

                Map.Entry entry = (Map.Entry) iterator.next();

            if (!topic.equals(entry.getKey())) {

                continue;
            }
                BrokerInfo brokerInfo = (BrokerInfo) brokerEntry.getKey();
                String ip = brokerInfo.getIp();
                String port = brokerInfo.getPort() + "";


                byte[] ipByte = ip.getBytes();
                byte[] portByte = port.getBytes();
                byte[] topicByte = topic.getBytes();


                byte ipByteLen = (byte) ipByte.length;
                byte portByteLen = (byte) portByte.length;
                byte topicNameByteLen = (byte) topicByte.length;


                int byteBufferLen = ipByte.length + portByte.length + topicByte.length + 3;
                byte[] allLengthByte = new byte[4];
                if (byteBufferLen > 127 * 127 * 127 * 127) {


                    throw new IndexOutOfBoundsException();

                } else if (byteBufferLen > 127 * 127 * 127) {
                    allLengthByte[0] = (byte) (byteBufferLen / (127 * 127 * 127));
                    allLengthByte[1] = (byte) (byteBufferLen / (allLengthByte[0] * 127 * 127));
                    allLengthByte[2] = (byte) (byteBufferLen / (allLengthByte[0] * allLengthByte[1] * 127));
                    allLengthByte[3] = (byte) (byteBufferLen / (allLengthByte[0] * allLengthByte[1] * allLengthByte[2]));
                } else if (byteBufferLen > 127 * 127) {
                    allLengthByte[0] = 0;
                    allLengthByte[1] = (byte) (byteBufferLen / (127 * 127));
                    allLengthByte[2] = (byte) (byteBufferLen / (allLengthByte[1] * 127));
                    allLengthByte[3] = (byte) (byteBufferLen / (allLengthByte[1] * allLengthByte[2]));
                } else if (byteBufferLen > 127) {
                    allLengthByte[0] = 0;
                    allLengthByte[1] = 0;
                    allLengthByte[2] = (byte) (byteBufferLen / 127);
                    allLengthByte[3] = (byte) (byteBufferLen / allLengthByte[2]);
                } else {
                    allLengthByte[0] = 0;
                    allLengthByte[1] = 0;
                    allLengthByte[2] = 0;
                    allLengthByte[3] = (byte) byteBufferLen;


                }


                heapBuffer.writeBytes(allLengthByte);


                heapBuffer.writeBytes(new byte[]{ipByteLen});
                heapBuffer.writeBytes(ipByte);
                heapBuffer.writeBytes(new byte[]{portByteLen});
                heapBuffer.writeBytes(portByte);
                heapBuffer.writeBytes(new byte[]{topicNameByteLen});
                heapBuffer.writeBytes(topicByte);


            }

                }



        return heapBuffer;
    }

    /*queueId,topic,offset,len*/
    public void decode(ByteBuf byteBuf){


//        byte[] data = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(data);
//        System.out.println(new String(data));


        while (byteBuf.isReadable()) {
            byte[] queueIdByteLen = new byte[1];
            byteBuf.readBytes(queueIdByteLen);
            int queueIdIntLen = queueIdByteLen[0];
            System.out.println("queueIdIntLen:" + queueIdByteLen);
            byte[] queueIdByte = new byte[queueIdIntLen];
            byteBuf.readBytes(queueIdByte);
            String queueId = new String(queueIdByte);

            byte[] topicByteLen = new byte[1];
            byteBuf.readBytes(topicByteLen);
            int topicIntLen = topicByteLen[0];
            System.out.println("topicIntLen:" + topicByteLen);
            byte[] topicByte = new byte[topicIntLen];
            byteBuf.readBytes(topicByte);
            String topic = new String(topicByte);


            long offset = byteBuf.readLong();
            long len = byteBuf.readLong();


            System.out.println(queueId);
            System.out.println(topic);
            System.out.println(offset);
            System.out.println(len);
            if (MessageInfoQueues.concurrentHashMap.containsKey(queueId)) {
                MessageInfoQueue messageInfoQueue = (MessageInfoQueue) MessageInfoQueues.concurrentHashMap.get(queueId);

                List list = messageInfoQueue.getList();
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setLen(len);
                messageInfo.setOffset(offset);
                messageInfo.setTopic(topic);
                list.add(messageInfo);


            } else {

                MessageInfoQueue messageInfoQueue = new MessageInfoQueue();
                MessageInfoQueues.concurrentHashMap.put(queueId, messageInfoQueue);

                List list = messageInfoQueue.getList();
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setLen(len);
                messageInfo.setOffset(offset);
                messageInfo.setTopic(topic);
                list.add(messageInfo);
            }

        }
        }

    }






