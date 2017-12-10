package io.openmessaging.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.BrokerConnectionCacheTable;

import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServer {

    private static NettyServer nettyServer = new NettyServer();

    private EventLoopGroup work = new NioEventLoopGroup();

    private EventLoopGroup boss = new NioEventLoopGroup();

    private NettyServer(){

    }

    public static NettyServer getNettyServer() {
        return nettyServer;
    }

    public static void setNettyServer(NettyServer nettyServer) {
        NettyServer.nettyServer = nettyServer;
    }

    public void bind(int port){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work);

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new NettyServerHandlerAdapter());
            }
        });

        ChannelFuture channelFuture = null;

        try {
            channelFuture = bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }

        if (channelFuture.isSuccess()) {
            System.out.println("Server connect success");


        }


    }


    //另开一个端口供Broker提交
    public void bindBroker(int port){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work);

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new BrokerTableHandlerAdapter());
            }
        });

        ChannelFuture channelFuture = null;

        try {
            channelFuture = bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }

        if (channelFuture.isSuccess()) {
            System.out.println("broker connect success");


        }


    }

    public Channel bind(BrokerInfo brokerInfo, final CountDownLatch countDownLatch){

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new UpdateTopicHandlerAdapter(countDownLatch));
            }
        });
        ChannelFuture channelFuture = null;

        try {
            channelFuture = bootstrap.connect(brokerInfo.getIp(),brokerInfo.getNameServerPort()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            work.shutdownGracefully();
        }
        if (channelFuture.isSuccess()) {
            System.out.println("nameServer connect success");


        }
        return  channelFuture.channel();


    }


    public void notifyBroker(BrokerInfo brokerInfo , ByteBuf byteBuf , CountDownLatch countDownLatch){

        Channel channel = (Channel) BrokerConnectionCacheTable.concurrentHashMap.get(brokerInfo);

        if (channel == null) {
        channel = bind(brokerInfo,countDownLatch);

        }

        if (channel == null) {
            System.out.println("send to broker fail");
            return;
        }
        channel.writeAndFlush(byteBuf);






    }




}
