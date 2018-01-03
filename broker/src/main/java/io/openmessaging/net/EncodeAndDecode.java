package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.broker.BrokerInfo;
import io.openmessaging.table.ConsumerIndexTable;
import io.openmessaging.table.MessageInfo;
import io.openmessaging.table.MessageInfoQueue;
import io.openmessaging.table.MessageInfoQueues;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * Created by fbhw on 17-12-5.
 */
public class EncodeAndDecode {

    private boolean discard = false;

    private int unReadNum = 0;

    private byte[] cacheBytes;

    private byte[] cachePullBytes;

    private int index = 0;

    private Lock lock = null;

    public EncodeAndDecode(Lock lock){

        this.lock = lock;

    }
    public EncodeAndDecode(){

    }


    //包括粘包处理
        public List decode(ByteBuf byteBuf){




            List list = new ArrayList();

            while (byteBuf.readableBytes() >= 4) {

                if (cacheBytes != null) {

                    byte[] arrayBuffer = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(arrayBuffer);
                    int newLen = cacheBytes.length + arrayBuffer.length;
                    byteBuf = Unpooled.buffer(newLen);
                    byteBuf.writeBytes(cacheBytes);
                    byteBuf.writeBytes(arrayBuffer);
                }


                //设置resetIndex标记
                byteBuf.markReaderIndex();
                byte[] allLength = new byte[4];
                byteBuf.readBytes(allLength);

                int allLenInt = 0;
                if (allLength[0] != 0 && allLength[1] != 0 && allLength[2] != 0 && allLength[3] != 0) {
                    allLenInt = allLength[0] * 127 * 127 * 127 + allLength[1] * 127 * 127 +  allLength[2] * 127 + allLength[3];


                } else if (allLength[1] != 0 && allLength[2] != 0 && allLength[3] != 0) {

                    allLenInt = allLength[1] * 127 * 127 +  allLength[2] * 127 + allLength[3];

                } else if (allLength[2] != 0 && allLength[3] != 0) {

                    allLenInt = allLength[2] * 127 + allLength[3];

                } else {
                    allLenInt = allLength[3];
                }








           /* if (allLenInt != 68) {

                    Map map = new HashMap(1);

                    String t = "被丢弃";

                    String q = "discard";

                    map.put(t,q);

                    setDiscard(false);

                    return map;



            }*/


                int remain = byteBuf.readableBytes();


                if (allLenInt == remain) {



                    byte[] topicByteLen = new byte[1];

                    byteBuf.readBytes(topicByteLen);

                    int topicIntLen = topicByteLen[0];

                    byte[] topicByte = new byte[topicIntLen];

                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);


                    byte[] queueIdByteLen = new byte[1];

                    byteBuf.readBytes(queueIdByteLen);

                    int queueIdIntLen = queueIdByteLen[0];

                    byte[] queueIdByte = new byte[queueIdIntLen];

                    byteBuf.readBytes(queueIdByte);

                    String queueId = new String(queueIdByte);

                    long sendTime = byteBuf.readLong();

                    byteBuf.resetReaderIndex();


                    byte[] data = new byte[allLenInt + 4];

                    byteBuf.readBytes(data);

                    Map map = new HashMap(1);

                    map.put("topic", topic);
                    map.put("queueId", queueId);
                    map.put("sendTime",sendTime);
                    map.put("data", data);

                    list.add(map);

                    cacheBytes = null;
                    return list;

                } else if (allLenInt < remain) {




                    byte[] topicByteLen = new byte[1];

                    byteBuf.readBytes(topicByteLen);

                    int topicIntLen = topicByteLen[0];

                    byte[] topicByte = new byte[topicIntLen];

                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);

                    byte[] queueIdByteLen = new byte[1];

                    byteBuf.readBytes(queueIdByteLen);

                    int queueIdIntLen = queueIdByteLen[0];

                    byte[] queueIdByte = new byte[queueIdIntLen];

                    byteBuf.readBytes(queueIdByte);


                    String queueId = new String(queueIdByte);



                    byteBuf.resetReaderIndex();
                    byte[] data = new byte[allLenInt + 4];
                    byteBuf.readBytes(data);


                    /*cacheBytes = new byte[byteBuf.readableBytes()];*/


                   /* byteBuf.readBytes(cacheBytes);*/

                    Map map = new HashMap(1);

                    map.put("topic", topic);
                    map.put("queueId", queueId);
                    map.put("data", data);


                    cacheBytes = null;
                    list.add(map);



               /* ip长度 1字节
                ip byte[]
                port转String长度 1字节
                port 转为byte[]*/


                } else {

                    if (cacheBytes != null) {

                        ByteBuf b = Unpooled.buffer(remain + 4 + cacheBytes.length);
                        b.writeBytes(cacheBytes);

                        byte[] data = new byte[remain + 4];
                        byteBuf.resetReaderIndex();
                        byteBuf.readBytes(data);
                        b.writeBytes(data);
                        cacheBytes = new byte[remain + 4 + cacheBytes.length];
                        b.readBytes(cacheBytes);


                    }
                    cacheBytes = new byte[remain + 4];
                    byteBuf.resetReaderIndex();
                    byteBuf.readBytes(cacheBytes);

                    return list;


                }


            }

            if (cacheBytes != null) {

                int remain = byteBuf.readableBytes();
                ByteBuf b = Unpooled.buffer(remain + cacheBytes.length);
                b.writeBytes(cacheBytes);

                byte[] data = new byte[remain];

                byteBuf.readBytes(data);
                b.writeBytes(data);
                cacheBytes = new byte[remain + cacheBytes.length];
                b.readBytes(cacheBytes);
                return list;

            }

            cacheBytes = new byte[byteBuf.readableBytes()];

            byteBuf.readBytes(cacheBytes);

            return list;
        }


        public ByteBuf encodeSendMessageBack(){
            byte[] sendResultBytes = "1".getBytes();
            ByteBuf byteBuf = Unpooled.buffer(sendResultBytes.length);

            byteBuf.writeBytes(sendResultBytes);

            return byteBuf;
        }



    public ByteBuf encodeBrokerInfo(BrokerInfo brokerInfo,ByteBuf byteBuf){
        String ip = brokerInfo.getIp();
        String producerPort = brokerInfo.getProducerPort() + "";
        String nameServerPort = brokerInfo.getNameServerPort() + "";
        String consumerPort = brokerInfo.getConsumerPort() + "";
        byte[] ipByte = ip.getBytes();

        int ipIntLen = ipByte.length;

        byte[] ipByteLen = new byte[1];

        ipByteLen[0] = (byte) ipIntLen;


        byte[] producerPortByte = producerPort.getBytes();
        int producerPortIntLen = producerPortByte.length;
        byte[] producerPortByteLen = new byte[1];
        producerPortByteLen[0] = (byte) producerPortIntLen;

        byte[] nameServerPortByte = nameServerPort.getBytes();
        int nameServerPortIntLen = nameServerPortByte.length;
        byte[] nameServerPortByteLen = new byte[1];
        nameServerPortByteLen[0] = (byte) nameServerPortIntLen;

        byte[] consumerPortByte = consumerPort.getBytes();
        int consumerPortIntLen = consumerPortByte.length;
        byte[] consumerPortByteLen = new byte[1];
        consumerPortByteLen[0] = (byte) consumerPortIntLen;



        byteBuf.writeBytes(ipByteLen);
        byteBuf.writeBytes(ipByte);
        byteBuf.writeBytes(producerPortByteLen);
        byteBuf.writeBytes(producerPortByte);

        byteBuf.writeBytes(nameServerPortByteLen);
        byteBuf.writeBytes(nameServerPortByte);

        byteBuf.writeBytes(consumerPortByteLen);
        byteBuf.writeBytes(consumerPortByte);


        return byteBuf;
    }
   /*ip,port,queueId,topic,offset,len*/
    public  ByteBuf  encodeToNameServer(BrokerInfo brokerInfo) {

        ByteBuf byteBuf = Unpooled.buffer(ConstantBroker.BUFFER_ROUTE_SIZE);


        byteBuf.markWriterIndex();

        byteBuf.writeBytes(new byte[4]);

        byteBuf = encodeConsumerIndex(byteBuf);
        if (MessageInfoQueues.concurrentHashMap.size() == 0) {
           byteBuf = encodeBrokerInfo(brokerInfo,byteBuf);

            int byteBufferLen = byteBuf.readableBytes() - 4;

            byte[] allLengthByte = new byte[4];
            if (byteBufferLen > 127 * 127 * 127 * 127) {


            }

            else if(byteBufferLen > 127 * 127 * 127){
                allLengthByte[0] = (byte) (byteBufferLen / (127 * 127 * 127));
                allLengthByte[1] = (byte) ((byteBufferLen - (allLengthByte[0] * (127 * 127 * 127))) / (127 * 127));
                allLengthByte[2] = (byte) ((byteBufferLen - (allLengthByte[0] * (127 * 127 * 127)) - (allLengthByte[1] * (127 * 127))) / 127);
                allLengthByte[3] = (byte) (byteBufferLen % 127);
            }
            else if(byteBufferLen > 127 * 127){
                allLengthByte[0] = 0;
                allLengthByte[1] = (byte) (byteBufferLen / (127 * 127));
                allLengthByte[2] = (byte) ((byteBufferLen - (allLengthByte[1] * (127 * 127))) / 127);
                allLengthByte[3] = (byte) (byteBufferLen % 127);
            }else if(byteBufferLen > 127){
                allLengthByte[0] = 0;
                allLengthByte[1] = 0;
                allLengthByte[2] = (byte) (byteBufferLen / 127);
                allLengthByte[3] = (byte) (byteBufferLen % 127);
            }else {
                allLengthByte[0] = 0;
                allLengthByte[1] = 0;
                allLengthByte[2] = 0;
                allLengthByte[3] = (byte) byteBufferLen;


            }



            int index = byteBuf.writerIndex();
            byteBuf.resetWriterIndex();

            byteBuf.writeBytes(allLengthByte);

            byteBuf.writerIndex(index);


            return byteBuf;

        }

        String ip = brokerInfo.getIp();
        String producerPort = brokerInfo.getProducerPort() + "";
        String nameServerPort = brokerInfo.getNameServerPort() + "";
        String consumerPort = brokerInfo.getConsumerPort() + "";

        byte[] ipByte = ip.getBytes();
        int ipIntLen = ipByte.length;
        byte[] ipByteLen = new byte[1];
        ipByteLen[0] = (byte) ipIntLen;

        byte[] producerPortByte = producerPort.getBytes();
        int producerPortIntLen = producerPortByte.length;
        byte[] producerPortByteLen = new byte[1];
        producerPortByteLen[0] = (byte) producerPortIntLen;

        byte[] nameServerPortByte = nameServerPort.getBytes();
        int nameServerPortIntLen = nameServerPortByte.length;
        byte[] nameServerPortByteLen = new byte[1];
        nameServerPortByteLen[0] = (byte) nameServerPortIntLen;

        byte[] consumerPortByte = consumerPort.getBytes();
        int consumerPortIntLen = consumerPortByte.length;
        byte[] consumerPortByteLen = new byte[1];
        consumerPortByteLen[0] = (byte) consumerPortIntLen;

        byteBuf.writeBytes(ipByteLen);
        byteBuf.writeBytes(ipByte);
        byteBuf.writeBytes(producerPortByteLen);
        byteBuf.writeBytes(producerPortByte);

        byteBuf.writeBytes(nameServerPortByteLen);
        byteBuf.writeBytes(nameServerPortByte);

        byteBuf.writeBytes(consumerPortByteLen);
        byteBuf.writeBytes(consumerPortByte);


        byteBuf.writeInt(MessageInfoQueues.concurrentHashMap.size());
            Set<String> set = MessageInfoQueues.concurrentHashMap.keySet();
            for (String topic : set){

                MessageInfoQueue e = MessageInfoQueues.concurrentHashMap.get(topic);
                //

                byte[] topicByte = topic.getBytes();

                byte topicByteLen = (byte) topicByte.length;
                MessageInfoQueue messageInfoQueue= e;

                String queueId = messageInfoQueue.getQueueId();




                byte[] queueIdByte = queueId.getBytes();
                byte queueIdByteLen = (byte) queueIdByte.length;


                byteBuf.writeBytes(new byte[]{topicByteLen});
                byteBuf.writeBytes(topicByte);
                byteBuf.writeBytes(new byte[]{queueIdByteLen});
                byteBuf.writeBytes(queueIdByte);



                List<MessageInfo> list = messageInfoQueue.getList();









                int incrementSaveIndex = messageInfoQueue.getIndex();

                int listSize = list.size();

                messageInfoQueue.setIndex(listSize);

                byteBuf.writeInt(listSize - incrementSaveIndex);




                for (int checkNum = incrementSaveIndex;checkNum < listSize;checkNum++) {
                    MessageInfo messageInfo = list.get(checkNum);
                    //
                    long offset = messageInfo.getOffset();


                    long len = messageInfo.getLen();


                    byteBuf.writeLong(offset);
                    byteBuf.writeLong(len);


                }



            }



        int byteBufferLen = byteBuf.readableBytes() - 4;

        byte[] allLengthByte = new byte[4];
        if (byteBufferLen > 127 * 127 * 127 * 127) {


        }

        else if(byteBufferLen > 127 * 127 * 127){
            allLengthByte[0] = (byte) (byteBufferLen / (127 * 127 * 127));
            allLengthByte[1] = (byte) ((byteBufferLen - (allLengthByte[0] * (127 * 127 * 127))) / (127 * 127));
            allLengthByte[2] = (byte) ((byteBufferLen - (allLengthByte[0] * (127 * 127 * 127)) - (allLengthByte[1] * (127 * 127))) / 127);
            allLengthByte[3] = (byte) (byteBufferLen % 127);
        }
        else if(byteBufferLen > 127 * 127){
            allLengthByte[0] = 0;
            allLengthByte[1] = (byte) (byteBufferLen / (127 * 127));
            allLengthByte[2] = (byte) ((byteBufferLen - (allLengthByte[1] * (127 * 127))) / 127);
            allLengthByte[3] = (byte) (byteBufferLen % 127);
        }else if(byteBufferLen > 127){
            allLengthByte[0] = 0;
            allLengthByte[1] = 0;
            allLengthByte[2] = (byte) (byteBufferLen / 127);
            allLengthByte[3] = (byte) (byteBufferLen % 127);
        }else {
            allLengthByte[0] = 0;
            allLengthByte[1] = 0;
            allLengthByte[2] = 0;
            allLengthByte[3] = (byte) byteBufferLen;


        }




        int i = byteBuf.writerIndex();
        byteBuf.resetWriterIndex();

        byteBuf.writeBytes(allLengthByte);

        byteBuf.writerIndex(i);


            return byteBuf;
        }

        public Map decodePull(ByteBuf byteBuf) {






                    byte[] magicNum = new byte[1];
                    byteBuf.readBytes(magicNum);

                    if (!"#".equals(new String(magicNum))) {
                        return null;
                    }

                    byte[] topicByteLen = new byte[1];
                    byteBuf.readBytes(topicByteLen);
                    int topicByteLenInt = topicByteLen[0];
                    byte[] topicByte = new byte[topicByteLenInt];
                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);
                    byte[] pullNumByte = new byte[1];
                    byteBuf.readBytes(pullNumByte);

                    int pullNum = pullNumByte[0];

                    byte[] uniqIdByteLen = new byte[1];

                    byteBuf.readBytes(uniqIdByteLen);

                    byte[] uniqIdByte = new byte[uniqIdByteLen[0]];

                    byteBuf.readBytes(uniqIdByte);

                    String uniqIdString = new String(uniqIdByte);

                    long uniqId = Long.parseLong(uniqIdString);

                    Map map = new HashMap(3);
                    map.put("topic",topic);
                    map.put("pullNum",pullNum);
                    map.put("uniqId",uniqId);



            return map;
        }


        public void decodeRestart(ByteBuf byteBuf){



            byte[] ipByteLen = new byte[1];
            byteBuf.readBytes(ipByteLen);
            int ipByteLenInt = ipByteLen[0];
            byteBuf.skipBytes(ipByteLenInt);

            byte[] producerPortByteLen = new byte[1];
            byteBuf.readBytes(producerPortByteLen);
            int producerPortByteLenInt = producerPortByteLen[0];
            byteBuf.skipBytes(producerPortByteLenInt);

            byte[] nameServerPortByteLen = new byte[1];
            byteBuf.readBytes(nameServerPortByteLen);
            int nameServerPortByteLenInt = nameServerPortByteLen[0];
            byteBuf.skipBytes(nameServerPortByteLenInt);

            byte[] consumerPortByteLen = new byte[1];
            byteBuf.readBytes(consumerPortByteLen);
            int consumerPortByteLenInt = consumerPortByteLen[0];
            byteBuf.skipBytes(consumerPortByteLenInt);

            byte[] topicByteLen = new byte[1];
            byteBuf.readBytes(topicByteLen);
            int topicByteLenInt = topicByteLen[0];

            byte[] topicByte = new byte[topicByteLenInt];
            byteBuf.readBytes(topicByte);

            byte[] queueIdByteLen = new byte[1];
            byteBuf.readBytes(queueIdByteLen);
            int queueIdByteLenInt = queueIdByteLen[0];

            byte[] queueIdByte = new byte[queueIdByteLenInt];
            byteBuf.readBytes(queueIdByte);

            long offset = byteBuf.readLong();
            long len = byteBuf.readLong();
            String topic = new String(topicByte);
            String queueId = new String(queueIdByte);


            Map map = MessageInfoQueues.concurrentHashMap;

            MessageInfoQueue messageInfoQueue = new MessageInfoQueue(topic);
            List list = messageInfoQueue.getList();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setOffset(offset);
            messageInfo.setLen(len);

            list.add(messageInfo);
            map.put(topic,messageInfoQueue);

            int mapSize = byteBuf.readInt();
            for (int checkNum = 0;checkNum < mapSize;checkNum++) {
                long consumerUniqId = byteBuf.readLong();
                int consumerIndex = byteBuf.readInt();
                ConsumerIndexTable.concurrentHashMap.put(consumerUniqId,consumerIndex);


            }

        }

        public ByteBuf encodeConsumerIndex(ByteBuf byteBuf){



                Set<Map.Entry<Long, Integer>> set = ConsumerIndexTable.concurrentHashMap.entrySet();

                int mapSize = ConsumerIndexTable.concurrentHashMap.size();

                byteBuf.writeInt(mapSize);
                for (Map.Entry entry : set) {

                    long uniqConcumerId = (long) entry.getKey();
                    int consumerIndex = (int) entry.getValue();

                    byteBuf.writeLong(uniqConcumerId);
                    byteBuf.writeInt(consumerIndex);
                }

            return byteBuf;
        }
        }



