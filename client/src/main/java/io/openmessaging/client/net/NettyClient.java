package io.openmessaging.client.net;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.producer.FactoryProducer;
import io.openmessaging.client.producer.NameServerInfo;
import io.openmessaging.client.table.SendQueues;
import io.openmessaging.client.table.ConnectionCacheNameServerTable;
import io.openmessaging.client.table.ConnectionCacheTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-11-25.
 */
public class NettyClient implements ConnectionHandler {

    private Map<BrokerInfo,Channel> connectionCacheTable = ConnectionCacheTable.getConnectionCacheTable();

    private Map<NameServerInfo,Channel> nameServerConnectionCacheTable = ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

    private SocketChannel socketChannel;

    private Channel channel;

    Logger logger = LoggerFactory.getLogger(FactoryProducer.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private CountDownLatch countDownLatchSendMessage = new CountDownLatch(1);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Bootstrap bootstrap = null;

    public NettyClient(){


    }


    public  Channel bind(BrokerInfo brokerInfo){
        Channel channel = null;
        if (( channel = connectionCacheTable.get(brokerInfo)) != null) {

            return channel;

        }


        String ip = brokerInfo.getIp();
        int port = brokerInfo.getPort();

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

                    socketChannel.pipeline().addLast(new NettyClientHandler(new SendResult(),countDownLatchSendMessage));

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

                socketChannel = (SocketChannel) future.channel();

                logger.info("client connect server success");

            }




        return channel;
    }

    public void sendSycn(Channel channel,ByteBuf byteBuf){


        System.out.println("准备发送");
        ChannelFuture channelFuture = channel.writeAndFlush(byteBuf);


        try {
            countDownLatchSendMessage.await();
        } catch (InterruptedException e) {

            e.printStackTrace();

            logger.error(e.getMessage());
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

                logger.info("client connect server success");

            }else {
                logger.info("client connect server fail");
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

    public void start(SendQueues sendQueues){
       java.util.Timer timer = new java.util.Timer();
       timer.schedule(new java.util.TimerTask() {
           @Override
           public void run() {
               sendQueues.updateListFromNameServer();
           }

       },0, ConstantClient.GET_LIST_TIMER_PERIOD);

    }






}
