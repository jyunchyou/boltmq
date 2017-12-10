package io.openmessaging.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.broker.BrokerInfo;
import io.openmessaging.nameserver.NameServerInfo;
import io.openmessaging.store.ConnectionCacheNameServerTable;

import javax.naming.Name;
import java.util.Map;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServer {

    private EventLoopGroup work = new NioEventLoopGroup();

    private EventLoopGroup boss = new NioEventLoopGroup();

    private Map<NameServerInfo,Channel> nameServerConnectionCacheTable = ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

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

    public void bindPullPort(int port){


        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work);

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new PullHandlerAdapter());
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
            System.out.println("producer connect success");


        }

    }


    public Channel bind(NameServerInfo nameServerInfo){
        System.out.println("bind");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new NettyServerHandlerAdapter());
            }
        });
        ChannelFuture channelFuture = null;

        try {
            channelFuture = bootstrap.connect(nameServerInfo.getIp(),nameServerInfo.getPort()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            work.shutdownGracefully();
        }
        if (channelFuture.isSuccess()) {
            System.out.println("nameServer connect success");


        }
       return  channelFuture.channel();


    }


    public void sendTableToNameServer(NameServerInfo nameServerInfo){

        Channel channel = nameServerConnectionCacheTable.get(nameServerInfo);
        if (channel == null) {

            channel = bind(nameServerInfo);
            nameServerConnectionCacheTable.put(nameServerInfo,channel);

        }


//            System.out.println("aaaa"+new String(routeByteBuffer.array()));



        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setIp(ConstantBroker.BROKER_MESSAGE_IP);
        brokerInfo.setNameServerPort(ConstantBroker.NAMESERVER_PORT);
        brokerInfo.setProducerPort(ConstantBroker.BROKER_MESSAGE_PORT);
        brokerInfo.setConsumerPort(ConstantBroker.PULL_PORT);
        ByteBuf byteBuf = encodeAndDecode.encodeToNameServer(brokerInfo);


        channel.writeAndFlush(byteBuf);

    }

    public void bindNameServerPort(int port){


        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work);

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new UpdateTopicHandlerAdapter());
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
            System.out.println("nameServer port bind success");


        }


    }



}
