package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.broker.BrokerInfo;
import io.openmessaging.nameserver.NameServerInfo;
import io.openmessaging.store.MessageInfo;
import io.openmessaging.store.MessageInfoQueue;
import io.openmessaging.store.MessageInfoQueues;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by fbhw on 17-12-5.
 */
public class EncodeAndDecode {

        //需要解析queueId,Topic字段

    private boolean discard = false;

    private int unReadNum = 0;

    private byte[] cacheBytes;

    private int index = 0;




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
                    allLenInt = allLength[0] * allLength[1] * allLength[2] * allLength[3];


                } else if (allLength[1] != 0 && allLength[2] != 0 && allLength[3] != 0) {

                    allLenInt = allLength[1] * allLength[2] * allLength[3];
                } else if (allLength[2] != 0 && allLength[3] != 0) {

                    allLenInt = allLength[2] * allLength[3];
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


                System.out.println("alllenInt:" + allLenInt);
                if (allLenInt == remain) {

                    System.out.println("==");


                    byte[] topicByteLen = new byte[1];

                    byteBuf.readBytes(topicByteLen);

                    int topicIntLen = topicByteLen[0];

                    byte[] topicByte = new byte[topicIntLen];

                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);


                    System.out.println(topic+"----------------------------");
                    byte[] queueIdByteLen = new byte[1];

                    byteBuf.readBytes(queueIdByteLen);

                    int queueIdIntLen = queueIdByteLen[0];

                    byte[] queueIdByte = new byte[queueIdIntLen];

                    byteBuf.readBytes(queueIdByte);

                    String queueId = new String(queueIdByte);

                    byteBuf.resetReaderIndex();


                    byte[] data = new byte[allLenInt + 4];

                    byteBuf.readBytes(data);

                    Map map = new HashMap(1);

                    map.put("topic", topic);
                    map.put("queueId", queueId);
                    map.put("data", data);

                    list.add(map);

                    cacheBytes = null;
                    return list;

                } else if (allLenInt < remain) {


                    System.out.println("<");


                    byte[] topicByteLen = new byte[1];

                    byteBuf.readBytes(topicByteLen);

                    int topicIntLen = topicByteLen[0];

                    byte[] topicByte = new byte[topicIntLen];

                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);

                    System.out.println(topic+"----------------------------");
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
    public ByteBuf encodeToNameServer(BrokerInfo brokerInfo) {


        ByteBuf byteBuf = Unpooled.buffer(ConstantBroker.BUFFER_ROUTE_SIZE);

        if (MessageInfoQueues.concurrentHashMap.size() == 0) {
           byteBuf = encodeBrokerInfo(brokerInfo,byteBuf);


            return byteBuf;
        }



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

                List<MessageInfo> list = messageInfoQueue.getList();


                if (list.size() == 0) {

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
                    byteBuf.writeBytes(new byte[]{topicByteLen});
                    byteBuf.writeBytes(topicByte);
                    byteBuf.writeBytes(new byte[]{queueIdByteLen});
                    byteBuf.writeBytes(queueIdByte);


                        System.out.println("queueId has output!!!!!!!!!");
                        continue;

                }

                for (MessageInfo messageInfo : list) {
                    System.out.println(messageInfo);

                    //
                    long offset = messageInfo.getOffset();


                    long len = messageInfo.getLen();



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




                    System.out.println(ip);
                    System.out.println(topic);
                    System.out.println(topicByteLen);
                    System.out.println(queueIdByteLen);
                    System.out.println(queueId);
                    System.out.println(offset);
                    System.out.println(len);

                    byteBuf.writeBytes(ipByteLen);
                    byteBuf.writeBytes(ipByte);
                    byteBuf.writeBytes(producerPortByteLen);
                    byteBuf.writeBytes(producerPortByte);

                    byteBuf.writeBytes(nameServerPortByteLen);
                    byteBuf.writeBytes(nameServerPortByte);

                    byteBuf.writeBytes(consumerPortByteLen);
                    byteBuf.writeBytes(consumerPortByte);
                    byteBuf.writeBytes(new byte[]{topicByteLen});
                    byteBuf.writeBytes(topicByte);
                    byteBuf.writeBytes(new byte[]{queueIdByteLen});
                    byteBuf.writeBytes(queueIdByte);

                    byteBuf.writeLong(offset);
                    byteBuf.writeLong(len);
                    System.out.println("queueId has output");


                }

            }
            if (byteBuf.readableBytes() == 0) {
                byteBuf = encodeBrokerInfo(brokerInfo,byteBuf);

            }
            return byteBuf;
        }

        }



