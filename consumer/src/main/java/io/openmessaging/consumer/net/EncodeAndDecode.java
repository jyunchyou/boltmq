package io.openmessaging.consumer.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.consumer.consumer.BrokerInfo;
import io.openmessaging.consumer.table.ReceiveMessageTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbhw on 17-12-7.
 */

public class EncodeAndDecode {


    private byte[] cacheBytes;

    /**
     *ipLen ip portLen port
     *
     */

    //路由ByteBuf待粘包处理
    public void decodeReceiveTable(ByteBuf byteBuf){




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

               /*>*/ else {
                    System.out.println(">");
                    cacheBytes = new byte[remain + 4];
                    byteBuf.resetReaderIndex();
                    byteBuf.readBytes(cacheBytes);

                    return ;
                }



                }


    }

}
