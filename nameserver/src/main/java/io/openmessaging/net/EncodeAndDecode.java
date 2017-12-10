package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-4.
 */
public class EncodeAndDecode {

    private TopicBrokerTable topicBrokerTable = new TopicBrokerTable();

    private BrokerInfoTable brokerInfoTable = new BrokerInfoTable();
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

            System.out.println("---------------------queueNum--------------------"+queueNum);
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
        System.out.println("topicByteLen:"+topicByte.length);
        System.out.println("mapSize:"+listSize);
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
    public  void decode(ByteBuf byteBuf){


//        byte[] data = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(data);
//        System.out.println(new String(data));


        while (byteBuf.isReadable()) {




            byte[] ipByteLen = new byte[1];
            byteBuf.readBytes(ipByteLen);
            int ipIntLen = ipByteLen[0];

            byte[] ipByte = new byte[ipIntLen];
            byteBuf.readBytes(ipByte);
            String ip = new String(ipByte);

            byte[] producerPortByteLen = new byte[1];
            byteBuf.readBytes(producerPortByteLen);
            int producerPortIntLen = producerPortByteLen[0];

            byte[] producerPortByte = new byte[producerPortIntLen];
            byteBuf.readBytes(producerPortByte);
            String producerPort = new String(producerPortByte);

            byte[] nameServerPortByteLen = new byte[1];
            byteBuf.readBytes(nameServerPortByteLen);
            int nameServerPortIntLen = nameServerPortByteLen[0];

            byte[] nameServerPortByte = new byte[nameServerPortIntLen];
            byteBuf.readBytes(nameServerPortByte);
            String nameServerPort = new String(nameServerPortByte);


            byte[] consumerPortByteLen = new byte[1];
            byteBuf.readBytes(consumerPortByteLen);
            int consumerPortIntLen = consumerPortByteLen[0];

            byte[] consumerPortByte = new byte[consumerPortIntLen];
            byteBuf.readBytes(consumerPortByte);
            String consumerPort = new String(consumerPortByte);


            if (!byteBuf.isReadable()) {

                putBroker(ip,producerPort,nameServerPort,consumerPort);
                return ;

            }

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


            System.out.println(topic + "-------------------------------------");

            long offset = 0;
            long len = 0;
            if (byteBuf.isReadable()) {

                 offset = byteBuf.readLong();
                 len = byteBuf.readLong();
            }





            putTopicBrokerTable(topic,ip,producerPort,nameServerPort,consumerPort,queueId);

            BrokerInfo brokerInfo = new BrokerInfo();
            brokerInfo.setIp(ip);
            brokerInfo.setProducerPort(Integer.parseInt(producerPort));
            brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
            brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));
            MessageInfoQueues messageInfoQueues = null;
            if (brokerInfoTable.map.containsKey(brokerInfo)){

                messageInfoQueues = (MessageInfoQueues) brokerInfoTable.map.get(brokerInfo);

            }else {

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
                    System.out.println("----list size is not 0--------"+list.size()+"-------");


                } else {

                    MessageInfoQueue messageInfoQueue = new MessageInfoQueue();
                    map.put(topic, messageInfoQueue);
                    messageInfoQueue.setQueueId(queueId);

                    List list = messageInfoQueue.getList();

                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setLen(len);
                    messageInfo.setOffset(offset);
                    list.add(messageInfo);
                    System.out.println("----list size is not 0--------"+list.size()+"-------");

                }
            }
        }


        public void putBroker(String ip,String producerPort,String nameServerPort,String consumerPort){


        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setIp(ip);
        brokerInfo.setProducerPort(Integer.parseInt(producerPort));
        brokerInfo.setNameServerPort(Integer.parseInt(nameServerPort));
        brokerInfo.setConsumerPort(Integer.parseInt(consumerPort));
        BrokerInfoTable.map.put(brokerInfo,new MessageInfoQueues());
        }

        public synchronized void putTopicBrokerTable(String topic,String ip,String producerPort,String nameServerPort,String consumerPort,String queueId) {

            System.out.println(topic + ip + producerPort + nameServerPort + consumerPort + queueId);
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






