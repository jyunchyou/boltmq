package io.openmessaging.client.net;


import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.openmessaging.client.common.SendCallBack;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.handler.ConnectionHandler;
import io.openmessaging.client.handler.NettyClientHandler;
import io.openmessaging.client.handler.UpdateFromNameServerHandler;
import io.openmessaging.client.handler.UpdateSendIndexHandler;
import io.openmessaging.client.producer.AbstractProducer;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.producer.FactoryProducer;
import io.openmessaging.client.producer.NameServerInfo;
import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;
import io.openmessaging.client.table.ConnectionCacheNameServerTable;
import io.openmessaging.client.table.ConnectionCacheTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbhw on 17-11-25.
 */
public class NettyClient implements ConnectionHandler {

    public static Map<String,Channel> channels = new ConcurrentHashMap<String, Channel>();

    private Map<BrokerInfo,Channel> connectionCacheTable = ConnectionCacheTable.getConnectionCacheTable();

    private Map<NameServerInfo,Channel> nameServerConnectionCacheTable = ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

    private Channel channel;

    Logger logger = LoggerFactory.getLogger(FactoryProducer.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private CountDownLatch countDownLatchSendMessage = new CountDownLatch(1);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Bootstrap bootstrap = null;

    private EventLoopGroup work = new NioEventLoopGroup();

    private EventLoopGroup boss = new NioEventLoopGroup();

    public NettyClient(){

    }



    /*public static NettyClient getNettyClient() {
        return nettyClient;
    }
*/
  /*  public static void setNettyClient(NettyClient nettyClient) {
        NettyClient.nettyClient = nettyClient;
    }
*/

    public void send(ByteBuf byteBuf){


    }




    public  void putChannel(Instance instance, final AbstractProducer abstractProducer){
        Channel channel = null;
        if (( channel = channels.get(instance.getInstanceId())) != null) {

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
                bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,ConstantClient.CHANNEL_TIMEOUT);


                //bootstrap.remoteAddress(ip,port);
            }catch (Exception e){
                eventLoopGroup.shutdownGracefully();
            }
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                socketChannel.pipeline().addLast(new NettyClientHandler(socketChannel,abstractProducer));

                socketChannel.pipeline().addLast(new IdleStateHandler(ConstantClient.CHANNEL_TIMEOUT,0,0, TimeUnit.MILLISECONDS));

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

            channels.put(instance.getIp() + instance.getPort(),socketChannel);

            logger.info("connect broker success");

        }




        return;
    }



    public  Channel bind(final BrokerInfo brokerInfo, CountDownLatch countDownLatch, final AbstractProducer abstractProducer){
        Channel channel = null;
        if (( channel = connectionCacheTable.get(brokerInfo)) != null) {

            return channel;

        }


        String ip = brokerInfo.getIp();
        int port = brokerInfo.getProducerPort();

        if (bootstrap == null) {

            try {
                bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,ConstantClient.CHANNEL_TIMEOUT);


                //bootstrap.remoteAddress(ip,port);
            }catch (Exception e){
                eventLoopGroup.shutdownGracefully();
        }
            }

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {

                    socketChannel.pipeline().addLast(new NettyClientHandler(socketChannel,abstractProducer));

                    socketChannel.pipeline().addLast(new IdleStateHandler(ConstantClient.CHANNEL_TIMEOUT,0,0, TimeUnit.MILLISECONDS));

                }
            });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(ip,port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel = future.channel();

            if (future.isSuccess()) {

                SocketChannel socketChannel = (SocketChannel) future.channel();

                logger.info("connect broker success");

            }




        return channel;
    }

    public void send(Channel channel, ByteBuf byteBuf, int delayTime, SendCallBack sendCallBack,CountDownLatch countDownLatch){

        ChannelFuture channelFuture = channel.writeAndFlush(byteBuf);

        if (sendCallBack != null) {

            try {
                sendCallBack.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (countDownLatchSendMessage != null) {
            try {
                countDownLatchSendMessage.await();
            } catch (InterruptedException e) {

                e.printStackTrace();

                logger.error(e.getMessage());
            }
        }

    }

    //nameServer
    public  Channel bind(NameServerInfo nameServerInfo){
        Channel channel = null;
        if (( channel = nameServerConnectionCacheTable.get(nameServerInfo)) != null) {

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

    public void sendRouteRequest(Channel channel){
        ByteBuf byteBuf = Unpooled.wrappedBuffer("getList".getBytes());
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

    public void start(final SendQueues sendQueues){



       java.util.Timer timer = new java.util.Timer();
       timer.schedule(new java.util.TimerTask() {
           @Override
           public void run() {
  //             sendQueues.updateListFromNameServer();
           }

       },0, ConstantClient.GET_LIST_TIMER_PERIOD);
    }




}
