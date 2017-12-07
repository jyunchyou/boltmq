package io.openmessaging.consumer.table;

import io.netty.channel.Channel;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.consumer.BrokerInfo;
import io.openmessaging.consumer.consumer.NameServerInfo;
import io.openmessaging.consumer.net.EncodeAndDecode;
import io.openmessaging.consumer.net.NettyConsumer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-7.
 */
public class ReceiveMessageTable {



    public static ConcurrentHashMap<String/*topic*/,List<BrokerInfo>> concurrentHashMap = new ConcurrentHashMap();

    private NameServerInfo nameServerInfo = new NameServerInfo();//先默认一台

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private NettyConsumer nettyConsumer = new NettyConsumer();

    public ReceiveMessageTable(){
        init();
    }

    public void init(){
        nameServerInfo.setIp(ConstantConsumer.NAMESERVER_IP);
        nameServerInfo.setPort(ConstantConsumer.NAMESERVER_PORT);
    }


    public void updateReceiveTableFromNameServer(String topic){
        Channel channel = ConnectionCacheNameServerTable.connectionCacheNameServerTable.get(nameServerInfo);
        if (channel == null) {

            channel = nettyConsumer.bind(nameServerInfo);

        }

        nettyConsumer.sendRouteRequest(channel,topic);
//            System.out.println("aaaa"+new String(routeByteBuffer.array()));


      //  encodeAndDecode.decodeNameServerRoute(routeByteBuf,messageQueues);


    }
}
