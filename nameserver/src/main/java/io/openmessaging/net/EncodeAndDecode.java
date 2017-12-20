package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.start.BrokerRestart;
import io.openmessaging.table.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-4.
 */
public class EncodeAndDecode {

    private TopicBrokerTable topicBrokerTable = new TopicBrokerTable();

    private BrokerInfoTable brokerInfoTable = new BrokerInfoTable();

    private BrokerRestart brokerRestart = new BrokerRestart();

    private byte[] bBuf = null;
    int allLenInt = 0;

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

        Set<Map.Entry<BrokerInfo, MessageInfoQueues>> set = BrokerInfoTable.map.entrySet();
//
        int brokerNum = BrokerInfoTable.map.size();
        byte brokerNumByte = (byte) brokerNum;

        heapBuffer.writeInt(brokerNum);

        for (Map.Entry e : set) {

            BrokerInfo brokerInfo = (BrokerInfo) e.getKey();
            String ip = brokerInfo.getIp();
            byte[] ipByte = ip.getBytes();
            byte[] ipByteLen = new byte[1];
            ipByteLen[0] = (byte) ipByte.length;

            String port = brokerInfo.getProducerPort() + "";
            byte[] portByte = port.getBytes();
            byte[] portByteLen = new byte[1];
            portByteLen[0] = (byte) portByte.length;

            heapBuffer.writeBytes(ipByteLen);
            heapBuffer.writeBytes(ipByte);
            heapBuffer.writeBytes(portByteLen);
            heapBuffer.writeBytes(portByte);



            MessageInfoQueues messageInfoQueues = (MessageInfoQueues) e.getValue();
            ConcurrentHashMap concurrentHashMap = messageInfoQueues.getConcurrentHashMap();
            //
            int queueNum = messageInfoQueues.getConcurrentHashMap().size();

            byte queueNumByte = (byte) queueNum;
            heapBuffer.writeInt(queueNum);
            Set<Map.Entry> s = concurrentHashMap.entrySet();
            for (Map.Entry entry : s) {
                String queueId = (String) entry.getKey();




                byte[] queueIdByte = queueId.getBytes();
                byte[] queueIdByteLen = new byte[1];
                queueIdByteLen[0] = (byte) queueIdByte.length;

                heapBuffer.writeBytes(queueIdByteLen);
                heapBuffer.writeBytes(queueIdByte);


            }

        }



        return heapBuffer;
    }


    //brokerInfo-map{Ｎ*(topicName-queueId)

    /**
     *ReceiveTable协议格式
     *
     *
     *
     *  topicName长度 1字节
     *  topicName byte[]
     *
     *个数
     brokerInfo {
     ip长度 1字节
     ip byte[]
     port转String长度 1字节
     port 转为byte[]
     }
     *
     *个数
     queueId长度 1字节
     queueId byte[]

     */
    public ByteBuf encodeReceiveTable(String topic){

        ByteBuf heapBuffer = Unpooled.buffer(ConstantNameServer.ROUTE_TABLE_BUFFER_SIZE);


        if (!topicBrokerTable.concurrentHashMap.containsKey(topic)) {
            return null;
        }
        List<Map<BrokerInfo, List<String>>> list = topicBrokerTable.concurrentHashMap.get(topic);

        if (list.size() == 0) {

            return null;
        }
        //
        byte[] topicByte = topic.getBytes();

        byte topicByteLen = (byte) topicByte.length;
        heapBuffer.writeBytes(new byte[]{topicByteLen});
        heapBuffer.writeBytes(topicByte);
        int listSize = list.size();
        byte mapSizeByte = (byte) listSize;
        heapBuffer.writeBytes(new byte[]{mapSizeByte});
        for (Map map : list) {

            Set<Map.Entry> set = map.entrySet();
            for (Map.Entry entry : set) {
                BrokerInfo brokerInfo = (BrokerInfo) entry.getKey();
                List<String> queueIds = (List) entry.getValue();
                //
                byte[] ipByte = brokerInfo.getIp().getBytes();
                byte ipByteLen = (byte) ipByte.length;
                String port = brokerInfo.getConsumerPort() + "";
                byte[] portByte = port.getBytes();
                byte portByteLen = (byte) portByte.length;

                heapBuffer.writeInt(ipByte.length);
                heapBuffer.writeBytes(ipByte);
                heapBuffer.writeBytes(new byte[]{portByteLen});
                heapBuffer.writeBytes(portByte);
                //
                int queueSize = queueIds.size();
                byte queueSizeByte = (byte) queueSize;
                heapBuffer.writeBytes(new byte[]{queueSizeByte});

                for (String queueId : queueIds) {







                    byte[] queueIdByte = queueId.getBytes();
                    byte queueIdByteLen = (byte) queueIdByte.length;

                    heapBuffer.writeBytes(new byte[]{queueIdByteLen});
                    heapBuffer.writeBytes(queueIdByte);


                }
            }
        }

        return heapBuffer;
    }

    /*queueId,topic,offset,len*/
    public synchronized Object decode(ByteBuf byteBuf){

        byteBuf.markReaderIndex();
        byte[] a = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(a);
        byteBuf.resetReaderIndex();




        if (bBuf != null) {
            ByteBuf b = Unpooled.buffer(bBuf.length + byteBuf.readableBytes());
            b.writeBytes(bBuf);
            byte[] byteBufByte = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(byteBufByte);

            b.writeBytes(byteBufByte);
            byteBuf = b;
        }




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




            if (byteBuf.readableBytes() < allLenInt) {
                byteBuf.resetReaderIndex();
              bBuf = new byte[byteBuf.readableBytes()];
              byteBuf.readBytes(bBuf);
              return null;


            }else if (byteBuf.readableBytes() > allLenInt) {
                ByteBuf temp = byteBuf;
                byteBuf = Unpooled.buffer(allLenInt);
                byte[] tempByte = new byte[allLenInt];
                temp.readBytes(tempByte);
                byteBuf.writeBytes(tempByte);
                bBuf = new byte[temp.readableBytes()];
                temp.readBytes(bBuf);

            }else if (byteBuf.readableBytes() == allLenInt) {
                bBuf = null;
            }







        String ip = null;
        String producerPort = null;
        String nameServerPort = null;
        String consumerPort = null;






            int mapSize = byteBuf.readInt();
            for (int indexNum = 0;indexNum < mapSize;indexNum++) {
                byteBuf.skipBytes(4);
                byteBuf.skipBytes(8);
            }








            byte[] ipByteLen = new byte[1];
            byteBuf.readBytes(ipByteLen);
            int ipIntLen = ipByteLen[0];

            byte[] ipByte = new byte[ipIntLen];
            byteBuf.readBytes(ipByte);
            ip = new String(ipByte);

            byte[] producerPortByteLen = new byte[1];
            byteBuf.readBytes(producerPortByteLen);
            int producerPortIntLen = producerPortByteLen[0];

            byte[] producerPortByte = new byte[producerPortIntLen];
            byteBuf.readBytes(producerPortByte);
            producerPort = new String(producerPortByte);

            byte[] nameServerPortByteLen = new byte[1];
            byteBuf.readBytes(nameServerPortByteLen);
            int nameServerPortIntLen = nameServerPortByteLen[0];

            byte[] nameServerPortByte = new byte[nameServerPortIntLen];
            byteBuf.readBytes(nameServerPortByte);
            nameServerPort = new String(nameServerPortByte);


            byte[] consumerPortByteLen = new byte[1];
            byteBuf.readBytes(consumerPortByteLen);
            int consumerPortIntLen = consumerPortByteLen[0];

            byte[] consumerPortByte = new byte[consumerPortIntLen];
            byteBuf.readBytes(consumerPortByte);
            consumerPort = new String(consumerPortByte);
            ByteBuf returnByteBuf = putBroker(ip, producerPort, nameServerPort, consumerPort);

///////////////////////////////////////////////////////////////////////////
            if (!byteBuf.isReadable()) {


               /* if (returnByteBuf != null) {
                    return returnByteBuf;
                }*/
                return ip + producerPort + nameServerPort + consumerPort;

            }
            int mSize = byteBuf.readInt();
            for (int checkNum = 0;checkNum < mSize;checkNum++) {

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
                putTopicBrokerTable(topic, ip, producerPort, nameServerPort, consumerPort, queueId);




                    int listSize = byteBuf.readInt();

                    if (listSize == 0) {
                        BrokerInfo brokerInfo = new BrokerInfo();
                        brokerInfo.setIp(ip);
                        brokerInfo.setProducerPort(Integer.parseInt(producerPort));
                        brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
                        brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));
                        MessageInfoQueues messageInfoQueues = null;
                        if (!brokerInfoTable.map.containsKey(brokerInfo)) {

                            messageInfoQueues = new MessageInfoQueues();

                            brokerInfoTable.map.put(brokerInfo, messageInfoQueues);

                        }else {
                            messageInfoQueues = brokerInfoTable.map.get(brokerInfo);
                        }
                        Map map = messageInfoQueues.getConcurrentHashMap();

                        if (map.containsKey(topic)) {

                            MessageInfoQueue messageInfoQueue = (MessageInfoQueue) map.get(topic);
                            messageInfoQueue.setQueueId(queueId);


                        } else {

                            MessageInfoQueue messageInfoQueue = new MessageInfoQueue();
                            map.put(topic, messageInfoQueue);
                            messageInfoQueue.setQueueId(queueId);





                        }
                    }

                    for (int index = 0;index < listSize;index++) {


                        long offset = 0;
                        long len = 0;

                        offset = byteBuf.readLong();
                        len = byteBuf.readLong();



                        BrokerInfo brokerInfo = new BrokerInfo();
                        brokerInfo.setIp(ip);
                        brokerInfo.setProducerPort(Integer.parseInt(producerPort));
                        brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
                        brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));
                        MessageInfoQueues messageInfoQueues = null;
                        if (brokerInfoTable.map.containsKey(brokerInfo)) {

                            messageInfoQueues = (MessageInfoQueues) brokerInfoTable.map.get(brokerInfo);

                        } else {

                            messageInfoQueues = new MessageInfoQueues();

                            brokerInfoTable.map.put(brokerInfo, messageInfoQueues);

                        }
                        Map map = messageInfoQueues.getConcurrentHashMap();


                        if (map.containsKey(topic)) {

                            MessageInfoQueue messageInfoQueue = (MessageInfoQueue) map.get(topic);
                            messageInfoQueue.setQueueId(queueId);
                            List list = messageInfoQueue.getList();
                            MessageInfo messageInfo = new MessageInfo();
                            messageInfo.setLen(len);
                            messageInfo.setOffset(offset);
                            list.add(messageInfo);


                        } else {

                            MessageInfoQueue messageInfoQueue = new MessageInfoQueue();
                            map.put(topic, messageInfoQueue);
                            messageInfoQueue.setQueueId(queueId);

                            List list = messageInfoQueue.getList();

                            MessageInfo messageInfo = new MessageInfo();
                            messageInfo.setLen(len);
                            messageInfo.setOffset(offset);
                            list.add(messageInfo);

                        }

                    }





            }





            return ip+producerPort+nameServerPort+consumerPort;
        }



        public ByteBuf putBroker(String ip,String producerPort,String nameServerPort,String consumerPort){


        BrokerInfo brokerInfo = new BrokerInfo();



        brokerInfo.setIp(ip);
        brokerInfo.setProducerPort(Integer.parseInt(producerPort));
        brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
        brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));

        //如果已存在，开启broker故障重启处理
        if (BrokerInfoTable.map.containsKey(brokerInfo)) {

            //ByteBuf byteBuf = brokerRestart.restart(ip,producerPort,nameServerPort,consumerPort);

            return null/*byteBuf*/;


        }


        BrokerInfoTable.map.put(brokerInfo,new MessageInfoQueues());
        return null;
        }

        public synchronized void putTopicBrokerTable(String topic,String ip,String producerPort,String nameServerPort,String consumerPort,String queueId) {

            List<Map<BrokerInfo, List<String>>> list = null;

            list = topicBrokerTable.concurrentHashMap.get(topic);


            if (list == null) {

                list = new ArrayList();
                topicBrokerTable.concurrentHashMap.put(topic, (List<Map<BrokerInfo, List<String>>>) list);

                Map map = new HashMap<BrokerInfo, List<String>>();
                BrokerInfo brokerInfo = new BrokerInfo();
                brokerInfo.setIp(ip);
                brokerInfo.setProducerPort(Integer.parseInt(producerPort));
                brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
                brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));


                List queueIds = new ArrayList();
                queueIds.add(queueId);
                map.put(brokerInfo, queueIds);
                list.add(map);

            } else {
                for (Map map : list) {

                    BrokerInfo brokerInfo = new BrokerInfo();
                    brokerInfo.setIp(ip);
                    brokerInfo.setProducerPort(Integer.parseInt(producerPort));
                    brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
                    brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));
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

     /*       if (topicBrokerTable.concurrentHashMap.containsKey(topic)) {

                list = topicBrokerTable.concurrentHashMap.get(topic);

            } else if (!topicBrokerTable.concurrentHashMap.containsKey(topic) || topicBrokerTable.concurrentHashMap.get(topic).size() == 0) {


                list = new ArrayList();
                topicBrokerTable.concurrentHashMap.put(topic, (List<Map<BrokerInfo, List<String>>>) list);

                Map map = new HashMap<BrokerInfo, List<String>>();
                BrokerInfo brokerInfo = new BrokerInfo();
                brokerInfo.setIp(ip);
                brokerInfo.setPort(Integer.parseInt(port));

   public void putTopicBrokerTable(String topic,String ip,String port,String queueId) {

            System.out.println(topic + ip + port + queueId);
            List<Map<BrokerInfo, List<String>>> list = null;

            list = topicBrokerTable.concurrentHashMap.get(topic);

            if (list == null) {

                list = new ArrayList();
                topicBrokerTable.concurrentHashMap.put(topic, (List<Map<BrokerInfo, List<String>>>) list);

                Map map = new HashMap<BrokerInfo, List<String>>();
                BrokerInfo brokerInfo = new BrokerInfo();
                brokerInfo.setIp(ip);
                brokerInfo.setPort(Integer.parseInt(port));


                List queueIds = new ArrayList();
                queueIds.add(queueId);
                map.put(brokerInfo, queueIds);
                list.add(map);

            } else {
                for (Map map : list) {

                    BrokerInfo brokerInfo = new BrokerInfo();
                    brokerInfo.setIp(ip);
                    brokerInfo.setPort(Integer.parseInt(port));

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

     /*       if (topicBrokerTable.concurrentHashMap.containsKey(topic)) {

                list = topicBrokerTable.concurrentHashMap.get(topic);

            } else if (!topicBrokerTable.concurrentHashMap.containsKey(topic) || topicBrokerTable.concurrentHashMap.get(topic).size() == 0) {


                list = new ArrayList();
                topicBrokerTable.concurrentHashMap.put(topic, (List<Map<BrokerInfo, List<String>>>) list);

                List queueIds = new ArrayList();
                queueIds.add(queueId);
                map.put(brokerInfo,queueIds);
                list.add(map);
            }

            for (Map map : list) {

                BrokerInfo brokerInfo = new BrokerInfo();
                brokerInfo.setIp(ip);
                brokerInfo.setPort(Integer.parseInt(port));

                List queueIds = null;
                if (map.containsKey(brokerInfo)) {
                    queueIds = (List) map.get(brokerInfo);


                } else {
                    queueIds = new ArrayList<String>();
                    queueIds.add(queueId);
                    map.put(brokerInfo, queueIds);
                }

                if (queueIds.contains(queueId)) {
                    queueIds.add(queueId);

                } else {
                    return;
                }
            }
        */
        }


}






