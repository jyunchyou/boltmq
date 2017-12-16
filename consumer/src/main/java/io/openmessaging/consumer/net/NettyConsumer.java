package io.openmessaging.consumer.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.openmessaging.consumer.constant.ConsumeModel;
import io.openmessaging.consumer.consumer.BrokerInfo;
import io.openmessaging.consumer.consumer.NameServerInfo;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.table.ConnectionCacheBrokerTabel;
import io.openmessaging.consumer.table.ConnectionCacheNameServerTable;
import io.openmessaging.consumer.table.TopicBrokerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-7.
 */
public class NettyConsumer {

    private static  NettyConsumer nettyConsumer = new NettyConsumer();

    Logger logger = LoggerFactory.getLogger(NettyConsumer.class);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private ConcurrentHashMap nameServerConnnectinoCacheTable = (ConcurrentHashMap) ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

    private Bootstrap bootstrap = null;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private NettyConsumer(){

    }

    public static NettyConsumer getNettyConsumer() {
        return nettyConsumer;
    }

    public static void setNettyConsumer(NettyConsumer nettyConsumer) {
        NettyConsumer.nettyConsumer = nettyConsumer;
    }

    public Channel bind(NameServerInfo nameServerInfo){

        Channel channel = null;
        if ((channel = (Channel) nameServerConnnectinoCacheTable.get(nameServerInfo)) != null) {

            return channel;
        }
        String ip = nameServerInfo.getIp();
        int port = nameServerInfo.getPort();

        if (bootstrap == null) {

            try {
                bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                //bootstrap.remoteAddress(ip,port);
            }catch (Exception e){

                eventLoopGroup.shutdownGracefully();

            }

        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                socketChannel.pipeline().addLast(new UpdateFromNameServerHandler(countDownLatch));
            }
        });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(ip,port).sync();
           /* future.channel().close().sync();*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (future.isSuccess()) {


            channel = (SocketChannel) future.channel();

            logger.info("client connect server success");

        }else {
            logger.info("client connect server fail");
        }
        return channel;

    }

    public void sendRouteRequest(Channel channel,String topic){
        ByteBuf byteBuf = Unpooled.wrappedBuffer(topic.getBytes());
        ChannelFuture channelFuture = channel.writeAndFlush(byteBuf);


        /*try {
            channelFuture.channel().close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return ;

    }


    public Channel bind(BrokerInfo brokerInfo, final int num, final ListenerMessage listenerMessage, final CountDownLatch countDownLatch){


        String ip = brokerInfo.getIp();
        int port = brokerInfo.getConsumerPort();
        Channel channel = null;
        Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                //bootstrap.remoteAddress(ip,port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                socketChannel.pipeline().addLast(new ReceiveMessageHandlerAdapter(num,listenerMessage,countDownLatch));
            }
        });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(ip,port).sync();
           /* future.channel().close().sync();*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (future.isSuccess()) {


           channel = (SocketChannel) future.channel();

            logger.info("client connect server success");

        }else {
            logger.info("client connect server fail");
        }
        return channel;



    }


    public void pull(String topic, int num, ListenerMessage listenerMessage, CountDownLatch countDownLatch,long uniqId) {

        while (TopicBrokerTable.concurrentHashMap.isEmpty()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        /**
         * 表结构 topic-List{BrokerInfo-List{queueId}}
         *
         * */
        List<Map<BrokerInfo, List<String>>> list = TopicBrokerTable.concurrentHashMap.get(topic);



        while (list == null || list.size() == 0) {

            System.out.println("list == 0 || == null");
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Map map : list) {
            BrokerInfo brokerInfo = (BrokerInfo) map.keySet().iterator().next();

            List queueIds = (List) map.get(brokerInfo);

            ByteBuf byteBuf = encodeAndDecode.encodePull(topic, num, queueIds,uniqId);

            Channel channel = ConnectionCacheBrokerTabel.connectionCacheBrokerTable.get(brokerInfo);


            if (channel != null) {
                Future future = channel.writeAndFlush(byteBuf);

                if (future.isSuccess()) {
                    logger.info("发送成功");
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else {
                    //logger.info("发送失败");


                }

            } else {


                Channel c = this.bind(brokerInfo,num,listenerMessage,countDownLatch);

                if (c == null) {

                    logger.info("连接失败,取消pull");

                    return;
                }

                ConnectionCacheBrokerTabel.connectionCacheBrokerTable.put(brokerInfo, c);

                Future future = c.writeAndFlush(byteBuf);
                if (future.isSuccess()) {
                    System.out.println("-------pull请求发送-----");

                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("------pull shibai-----");
                }


            }
        }


    }


}
