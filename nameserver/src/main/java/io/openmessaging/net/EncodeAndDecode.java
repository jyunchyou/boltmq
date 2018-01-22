package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.filter.EncodeAndDecodeFilter;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.start.BrokerRestart;
import io.openmessaging.table.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-4.
 */
public class EncodeAndDecode {


    private BrokerRestart brokerRestart = new BrokerRestart();

    private EncodeAndDecodeFilter encodeAndDecodeFilter = new EncodeAndDecodeFilter();

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


        Map map = encodeAndDecodeFilter.filterBrokerTopicTable(BrokerTopicTable.concurrentHashMap);
        Set<Map.Entry<BrokerInfo, ArrayList<String>>> set = map.entrySet();

        int brokerSize = map.size();


        System.out.println("brokerSize:"+brokerSize);

        byte brokerSizeByte = (byte) brokerSize;
        heapBuffer.writeByte(brokerSizeByte);
        for (Map.Entry e : set) {
            BrokerInfo brokerInfo = (BrokerInfo) e.getKey();
            String ip = brokerInfo.getIp();

            String port = brokerInfo.getProducerPort() + "";
            byte[] ipByte = ip.getBytes();
            byte[] portByte = port.getBytes();
            byte ipByteLen = (byte) ipByte.length;
            byte portByteLen = (byte) portByte.length;
            heapBuffer.writeByte(ipByteLen);
            heapBuffer.writeBytes(ipByte);
            heapBuffer.writeByte(portByteLen);
            heapBuffer.writeBytes(portByte);

            ArrayList<String> arrayList = (ArrayList) e.getValue();

            System.out.println("topicSize:"+arrayList.size());


            int topicSize = arrayList.size();
            byte topicSizeByte = (byte) topicSize;
            heapBuffer.writeByte(topicSizeByte);
            for (String topic : arrayList) {
                byte[] topicByte = topic.getBytes();
                byte topicByteLen = (byte) topicByte.length;
                heapBuffer.writeByte(topicByteLen);

                heapBuffer.writeBytes(topicByte);
            }
        }


        return heapBuffer;
}

/*

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
*/




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


        if (!TopicBrokerTable.concurrentHashMap.containsKey(topic)) {
            return null;
        }
        List<Map<BrokerInfo, List<String>>> list = TopicBrokerTable.concurrentHashMap.get(topic);

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

    //解析broker map
    public synchronized String decode(ByteBuf byteBuf){





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
            //putBroker(ip, producerPort, nameServerPort, consumerPort);

///////////////////////////////////////////////////////////////////////////
            if (!byteBuf.isReadable()) {


               /* if (returnByteBuf != null) {
                    return returnByteBuf;
                }*/
               putBroker(ip,producerPort,nameServerPort,consumerPort);
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


                putTopicBrokerTable(topic, ip, producerPort, nameServerPort, consumerPort);










            }





            return ip+producerPort+nameServerPort+consumerPort;
        }



        public void putBroker(String ip,String producerPort,String nameServerPort,String consumerPort){


        BrokerInfo brokerInfo = new BrokerInfo();



        brokerInfo.setIp(ip);
        brokerInfo.setProducerPort(Integer.parseInt(producerPort));
        brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
        brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));


        if (BrokerTopicTable.concurrentHashMap.containsKey(brokerInfo)) {

            //ByteBuf byteBuf = brokerRestart.restart(ip,producerPort,nameServerPort,consumerPort);

            return;


        }


        BrokerTopicTable.concurrentHashMap.put(brokerInfo,new ArrayList<String>());

        }

        public synchronized void putTopicBrokerTable(String topic,String ip,String producerPort,String nameServerPort,String consumerPort) {

            List<Map<BrokerInfo, List<String>>> list = null;


            //set BrokerTopicTable

            BrokerInfo brokerInfo = new BrokerInfo();
            brokerInfo.setIp(ip);
            brokerInfo.setProducerPort(Integer.parseInt(producerPort));
            brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
            brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));

            ArrayList arrayList = BrokerTopicTable.concurrentHashMap.get(brokerInfo);
            if (arrayList == null) {
                arrayList = new ArrayList();
            }

            if (!arrayList.contains(topic)) {
                arrayList.add(topic);

            }
            //set TopicBrokerTable

            list = TopicBrokerTable.concurrentHashMap.get(topic);
            Map map = new HashMap<BrokerInfo, List<String>>(1);

            List topics = new ArrayList();
            topics.add(topic);
            map.put(brokerInfo,topics);


            if (list == null) {
                list = new ArrayList<Map<BrokerInfo, List<String>>>();
                TopicBrokerTable.concurrentHashMap.put(topic, list);

            }else {


                TopicBrokerTable.concurrentHashMap.put(topic,list);






            }
            list.add(map);

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






