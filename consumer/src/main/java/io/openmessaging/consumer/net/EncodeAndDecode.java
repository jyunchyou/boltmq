package io.openmessaging.consumer.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.constant.ConsumeModel;
import io.openmessaging.consumer.consumer.BrokerInfo;
import io.openmessaging.consumer.consumer.Message;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import io.openmessaging.consumer.table.TopicBrokerTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fbhw on 17-12-7.
 */

public class EncodeAndDecode {


    private byte[] cacheBytes;

    /**
     *ipLen ip portLen port
     *
     */


    public void decodeReceiveTable(ByteBuf heapBuffer){







            byte[] topicByteLen = new byte[1];
            heapBuffer.readBytes(topicByteLen);
            int topicByteLenInt = topicByteLen[0];

            byte[] topicByte = new byte[topicByteLenInt];
            heapBuffer.readBytes(topicByte);
            String topic = new String(topicByte);
            byte[] mapSizeByte = new byte[1];
            heapBuffer.readBytes(mapSizeByte);
            int mapSize = mapSizeByte[0];
        System.out.println("topicByteLen:"+topicByte.length);
        System.out.println("mapSize:"+mapSize);
            for (int indexNum = 0; indexNum < mapSize; indexNum++) {

                int ipByteLenInt = heapBuffer.readInt();
                byte[] ipByte = new byte[ipByteLenInt];
                heapBuffer.readBytes(ipByte);
                String ip = new String(ipByte);

                byte[] portByteLen = new byte[1];
                heapBuffer.readBytes(portByteLen);
                int portByteLenInt = portByteLen[0];
                byte[] portByte = new byte[portByteLenInt];
                heapBuffer.readBytes(portByte);
                String port = new String(portByte);

                byte[] queueSizeByte = new byte[1];
                heapBuffer.readBytes(queueSizeByte);
                int queueSize = queueSizeByte[0];
                System.out.println(queueSize);

                for (int checkNum = 0; checkNum < queueSize; checkNum++) {


                    byte[] queueIdByteLen = new byte[1];
                    heapBuffer.readBytes(queueIdByteLen);
                    int queueIdByteLenInt = queueIdByteLen[0];
                    byte[] queueIdByte = new byte[queueIdByteLenInt];
                    heapBuffer.readBytes(queueIdByte);
                    String queueId = new String(queueIdByte);

                    putTopicBrokerTable(topic, ip, port, queueId);


                }

            }



        }



/*

        while (byteBuf.readableBytes() >= 4) {


            System.out.println("while");

                if (cacheBytes != null) {

                    byte[] arrayBuffer = new byte[byteBuf.readableBytes()];
                    System.out.println("cacheBytes.length" + cacheBytes.length);
                    System.out.println("byteBuf.readableBytes()" + byteBuf.readableBytes());
                    byteBuf.readBytes(arrayBuffer);
                    int newLen = cacheBytes.length + byteBuf.readableBytes();
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

                int remain = byteBuf.readableBytes();



                if (allLenInt <= remain) {



//ip
                    byte[] ipLen = new byte[1];
                    byteBuf.readBytes(ipLen);
                    int ipLenInt = ipLen[0];
                    byte[] ipByte = new byte[ipLenInt];
                    byteBuf.readBytes(ipByte);
                    String ip = new String(ipByte);
//port
                    byte[] portLen = new byte[1];
                    byteBuf.readBytes(portLen);
                    int portLenInt = portLen[0];
                    byte[] portByte = new byte[portLenInt];
                    byteBuf.readBytes(portByte);
                    int port = Integer.parseInt(new String(portByte));
//topic
                    byte[] topicLen = new byte[1];
                    byteBuf.readBytes(topicLen);
                    int topicLenInt = topicLen[0];
                    byte[] topicByte = new byte[topicLenInt];
                    byteBuf.readBytes(topicByte);
                    String topic = new String(topicByte);


                    System.out.println(ip+port+topic);


                    if (ReceiveMessageTable.concurrentHashMap.containsKey(topic)) {


                        List<BrokerInfo> list = ReceiveMessageTable.concurrentHashMap.get(topic);

                        list.clear();

                        BrokerInfo brokerInfo = new BrokerInfo();
                        brokerInfo.setIp(ip);
                        brokerInfo.setPort(port);
                        list.add(brokerInfo);
                    } else {
                        List<BrokerInfo> list = new ArrayList<BrokerInfo>();


                        BrokerInfo brokerInfo = new BrokerInfo();
                        brokerInfo.setIp(ip);
                        brokerInfo.setPort(port);
                        list.add(brokerInfo);

                        ReceiveMessageTable.concurrentHashMap.put(topic, list);
                    }

                    if (allLenInt == remain) {
                        System.out.println("return");
                        return ;
                    }else {
                        cacheBytes = null;
                    }

                }

               */
/*>*//*
 else {
                    System.out.println(">");
                    cacheBytes = new byte[remain + 4];
                    byteBuf.resetReaderIndex();
                    byteBuf.readBytes(cacheBytes);

                    return ;
                }



                }

*/



    public void putTopicBrokerTable(String topic,String ip,String port,String queueId) {

        System.out.println(topic + ip + port + queueId);
        List<Map<BrokerInfo, List<String>>> list = null;

        list = TopicBrokerTable.concurrentHashMap.get(topic);

        if (list == null) {

            list = new ArrayList();
            TopicBrokerTable.concurrentHashMap.put(topic, (List<Map<BrokerInfo, List<String>>>) list);

            Map map = new HashMap<BrokerInfo, List<String>>();
            BrokerInfo brokerInfo = new BrokerInfo();
            brokerInfo.setIp(ip);
            brokerInfo.setConsumerPort(Integer.parseInt(port));


            List queueIds = new ArrayList();
            queueIds.add(queueId);
            map.put(brokerInfo, queueIds);
            list.add(map);

        } else {
            for (Map map : list) {

                BrokerInfo brokerInfo = new BrokerInfo();
                brokerInfo.setIp(ip);
                brokerInfo.setConsumerPort(Integer.parseInt(port));

                List queueIds = null;

                queueIds = (List) map.get(brokerInfo);

                if (queueIds != null) {

                    if (queueIds.contains(queueId)) {
                        return;
                    } else {
                        queueIds.add(queueId);
                    }

                } else {
                    queueIds = new ArrayList();
                    queueIds.add(queueId);
                    map.put(brokerInfo, queueIds);


                }

            }

        }


    }

    /**
     * 协议格式
     * topicByteLen     1 byte
     * topicByte        String

     * pullNumByteLen   1 byte
     * pullNum          int
     * uniqId           long
     */

    public ByteBuf encodePull(String topic, int num, List<String> queueIds,long uniqId){

        byte[] magicNum = "#".getBytes();
        byte[] topicByte = topic.getBytes();
        byte topicByteLen = (byte) topicByte.length;

        byte numByte = (byte) num;

        ByteBuf byteBuf = Unpooled.buffer(topicByteLen + 1);

        String uniqIdString = new String(String.valueOf(uniqId));

        byte[] uniqIdByte = uniqIdString.getBytes();

        byte uniqIdByteLen = (byte) uniqIdByte.length;

        byteBuf.writeBytes(magicNum);
        byteBuf.writeBytes(new byte[]{topicByteLen});
        byteBuf.writeBytes(topicByte);

        byteBuf.writeBytes(new byte[]{numByte});

        byteBuf.writeBytes(new byte[]{uniqIdByteLen});
        byteBuf.writeBytes(uniqIdByte);

//        for (String queueId : queueIds) {
//
//            byte[] queueIdByte = queueId.getBytes();
//            byte queueIdByteLen = (byte) queueIdByte.length;
//
//            byteBuf.writeBytes(new byte[]{queueIdByteLen});
//            byteBuf.writeBytes(queueIdByte);
//        }



        return byteBuf;

    }


    /**
     *
     byteBuf.writeBytes(allLengthByte);

     //byteBuf.writeBytes(new byte[]{topicLen});
     //byteBuf.writeBytes(topic);
     byteBuf.writeBytes(new byte[]{queueIdByteLen});
     byteBuf.writeBytes(queueId);
     //byteBuf.writeBytes(bodyLen);
     //byteBuf.writeBytes(body);
     //byteBuf.writeBytes(new byte[]{orderIdLen});
     //byteBuf.writeBytes(orderId);
     byteBuf.writeBytes(new byte[]{idLen});
     byteBuf.writeBytes(id);
     byteBuf.writeBytes(new byte[]{languageLen});
     byteBuf.writeBytes(language);
     byteBuf.writeBytes(new byte[]{versionLen});
     byteBuf.writeBytes(version);
     byteBuf.writeBytes(new byte[]{serialModelLen});
     byteBuf.writeBytes(serialModel);
     byteBuf.writeBytes(new byte[]{codeLen});
     byteBuf.writeBytes(new byte[]{code});
     byteBuf.writeBytes(new byte[]{delayTimeLen});
     byteBuf.writeBytes(new byte[]{delayTime});
     */
    public List decodeMessage(ByteBuf byteBuf,int pullNum){

        int testByteBufLen = byteBuf.readableBytes();



        List list = new ArrayList(pullNum);

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

            System.out.println(new String(allLength));
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


                System.out.println("listSize:"+list.size());
                System.out.println("remain"+remain);
                System.out.println("bytebufLen:"+testByteBufLen);


            if (allLenInt == remain) {

                cacheBytes = null;
                System.out.println("==");

//topic
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


//body
                byte[] bodyLength = new byte[4];
                byteBuf.readBytes(bodyLength);

                int bodyLenInt = 0;
                if (bodyLength[0] != 0 && bodyLength[1] != 0 && bodyLength[2] != 0 && bodyLength[3] != 0) {
                    bodyLenInt = bodyLength[0] * bodyLength[1] * bodyLength[2] * bodyLength[3];


                } else if (bodyLength[1] != 0 && bodyLength[2] != 0 && bodyLength[3] != 0) {

                    bodyLenInt = bodyLength[1] * bodyLength[2] * bodyLength[3];
                } else if (bodyLength[2] != 0 && bodyLength[3] != 0) {

                    bodyLenInt = bodyLength[2] * bodyLength[3];
                } else {
                    bodyLenInt = bodyLength[3];
                }

                byte[] body = new byte[bodyLenInt];
                byteBuf.readBytes(body);
//order
                byte[] orderByteLen = new byte[1];

                byteBuf.readBytes(orderByteLen);

                int orderIntLen = orderByteLen[0];

                byte[] orderByte = new byte[orderIntLen];

                byteBuf.readBytes(orderByte);

                String order = new String(orderByte);
                Message message = new Message(topic,order,body);
                list.add(message);
                return  list;

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





//body
                byte[] bodyLength = new byte[4];
                byteBuf.readBytes(bodyLength);

                int bodyLenInt = 0;
                if (bodyLength[0] != 0 && bodyLength[1] != 0 && bodyLength[2] != 0 && bodyLength[3] != 0) {
                    bodyLenInt = bodyLength[0] * bodyLength[1] * bodyLength[2] * bodyLength[3];


                } else if (bodyLength[1] != 0 && bodyLength[2] != 0 && bodyLength[3] != 0) {

                    bodyLenInt = bodyLength[1] * bodyLength[2] * bodyLength[3];
                } else if (bodyLength[2] != 0 && bodyLength[3] != 0) {

                    bodyLenInt = bodyLength[2] * bodyLength[3];
                } else {
                    bodyLenInt = bodyLength[3];
                }

                byte[] body = new byte[bodyLenInt];
                byteBuf.readBytes(body);

//order
                byte[] orderByteLen = new byte[1];

                byteBuf.readBytes(orderByteLen);

                int orderIntLen = orderByteLen[0];

                byte[] orderByte = new byte[orderIntLen];

                byteBuf.readBytes(orderByte);

                String order = new String(orderByte);
                Message message = new Message(topic,order,body);
                list.add(message);

                byteBuf.resetReaderIndex();

                byteBuf.skipBytes(allLenInt + 4);



                    /*cacheBytes = new byte[byteBuf.readableBytes()];*/


                   /* byteBuf.readBytes(cacheBytes);*/


                cacheBytes = null;



               /* ip长度 1字节
                ip byte[]
                port转String长度 1字节
                port 转为byte[]*/


            } else {


                System.out.println(">");
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


}
