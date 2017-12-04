package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.BrokerTopicTable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by fbhw on 17-12-4.
 */
public class EncodeAndDecode {

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



        }


