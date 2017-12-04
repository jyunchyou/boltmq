package io.openmessaging.client.producer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.Constant;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.net.EncodeAndDecode;
import io.openmessaging.client.net.NettyClient;
import io.openmessaging.client.table.ConnectionCacheNameServerTable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fbhw on 17-11-5.
 */
public class SendQueues {


   static List messageQueues = new ArrayList<SendQueue>();

   private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

   private NameServerInfo nameServerInfo = new NameServerInfo();

   private Map<NameServerInfo,Channel> nameServerConnectionCacheTable = ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

   private NettyClient nettyClient = new NettyClient();

   public static ByteBuf routeByteBuf = null;

   public SendQueues() throws IOException {

        init();
    }

    public void init(){
        nameServerInfo.setIp(ConstantClient.NAMESERVER_IP);
        nameServerInfo.setPort(ConstantClient.NAMESERVER_PORT);
    }

//更新方式：clear整个list,一次扩容,多次使用
    //方法流程:
    // 1.先查看nameServer是否有连接缓存,如果没有的话通过NettyClient创建nameServer的channel;
    //2.如果有,则继续通过NettyClient拿到路由信息的byteBuffer;
    //3.最后通过encodeAndDecode解析为SendQueues.定时更新就是重复2,3步

    public List getList(){
        if (messageQueues.size() == 0) {
            //TODO get byteBuffer and channel  from nameServer


            Channel channel = nameServerConnectionCacheTable.get(nameServerInfo);
            if (channel == null) {

                channel = nettyClient.bind(nameServerInfo);

            }

            nettyClient.sendRouteRequest(channel);
//            System.out.println("aaaa"+new String(routeByteBuffer.array()));


           messageQueues = encodeAndDecode.decodeNameServerRoute(routeByteBuf,messageQueues);


        }
        return messageQueues;

    }
}
