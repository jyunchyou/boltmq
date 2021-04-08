package io.openmessaging.consumer.net;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.openmessaging.consumer.broker.BrokerInfo;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.consumer.ConsumeCallBack;
import io.openmessaging.consumer.consumer.FactoryConsumer;
import io.openmessaging.consumer.handler.ReceiveMessageHandlerAdapter;
import io.openmessaging.consumer.handler.UpdateFromNameServerHandler;
import io.openmessaging.consumer.nameserver.NameServerInfo;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.table.ConnectionCacheBrokerTabel;
import io.openmessaging.consumer.table.ConnectionCacheNameServerTable;
import io.openmessaging.consumer.table.TopicBrokerTable;
import io.openmessaging.consumer.util.InfoCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbhw on 17-12-7.
 */
public class NettyConsumer {

    public static  NettyConsumer nettyConsumer = new NettyConsumer();

    Logger logger = LoggerFactory.getLogger(NettyConsumer.class);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private ConcurrentHashMap nameServerConnnectinoCacheTable = (ConcurrentHashMap) ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

    private Bootstrap bootstrap = null;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    //key 为topic + brokerId
    public static Map<String,Channel> channelMap = new ConcurrentHashMap<String,Channel>();

    public static Map<String,ConsumeCallBack> consumeCallBackMap = new ConcurrentHashMap<String, ConsumeCallBack>();

    //key 为topic + brokerId
    public static Map<String,Long> consumeIndexMap = new ConcurrentHashMap<String, Long>();//broker消费下标

    private NettyConsumer(){

    }

    public static NettyConsumer getNettyConsumer() {
        return nettyConsumer;
    }

    public static void setNettyConsumer(NettyConsumer nettyConsumer) {
        NettyConsumer.nettyConsumer = nettyConsumer;
    }

    public  void putNewChannel(final String topic,final Instance instance, final String consumerName,final int model){

        Channel channel = null;
        if (channelMap.containsKey(topic + instance.getInstanceId())) {

            return;

        }


        String ip = instance.getIp();
        int port = instance.getPort();

        if (bootstrap == null) {

            try {
                bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ConstantConsumer.CHANNEL_TIMEOUT);
                //bootstrap.remoteAddress(ip,port);
            }catch (Exception e){
                eventLoopGroup.shutdownGracefully();
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new ReceiveMessageHandlerAdapter(topic,instance.getInstanceId(),consumerName,model));
                socketChannel.pipeline().addLast(new IdleStateHandler(ConstantConsumer.CHANNEL_TIMEOUT,0,0, TimeUnit.MILLISECONDS));
            }
        });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(ip,port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (future.isSuccess()) {
            SocketChannel socketChannel = (SocketChannel) future.channel();
            channelMap.put(topic + instance.getInstanceId(),socketChannel);
            //127.0.0.1#8100#brokers#DEFAULT_GROUP@@broker2
            logger.info("connect broker success");
//127.0.0.1#8099#brokers#DEFAULT_GROUP@@broker2
        }







        return;
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

            logger.info("connect nameserver success");

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

/*
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

                socketChannel.pipeline().addLast(new ReceiveMessageHandlerAdapter());
            }
        });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(ip,port).sync();
           *//* future.channel().close().sync();*//*
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (future.isSuccess()) {


           channel = (SocketChannel) future.channel();

            logger.info("connect broker success");

        }
        return channel;



    }*/

/*

    public void pull(String topic, int num, ListenerMessage listenerMessage, CountDownLatch countDownLatch,long uniqId) throws NacosException, InterruptedException {

        while (TopicBrokerTable.concurrentHashMap.isEmpty()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        Instance instance = FactoryConsumer.naming.selectOneHealthyInstance("broker");//获取所有broker


            BrokerInfo brokerInfo1 = InfoCast.cast(instance);




        */
/**
         * 表结构 topic-List{BrokerInfo-List{queueId}}
         *
         * *//*

        List<Map<BrokerInfo, List<String>>> list = TopicBrokerTable.concurrentHashMap.get(topic);



        while (list == null || list.size() == 0) {

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Map map : list) {
            BrokerInfo brokerInfo = (BrokerInfo) map.keySet().iterator().next();

            ByteBuf byteBuf = encodeAndDecode.encodePull(topic, num, uniqId);

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

                        countDownLatch.await();
                } else {
                }


            }
        }


    }
*/


}
