package io.openmessaging.net;

import com.alibaba.nacos.api.naming.pojo.Instance;
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
import io.openmessaging.handler.*;
import io.openmessaging.broker.NameServerInfo;
import io.openmessaging.table.ConnectionCacheNameServerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServer {
    private NettyServer(){
    }
    public static NettyServer nettyServer = new NettyServer();

    Logger logger = LoggerFactory.getLogger("NettyServer");

    private EventLoopGroup work = new NioEventLoopGroup();

    private EventLoopGroup boss = new NioEventLoopGroup();

    private Map<NameServerInfo,Channel> nameServerConnectionCacheTable = ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

    private Lock lock = new ReentrantLock(false);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode(lock);

    public static ConcurrentHashMap ConsumeIndexMap = new ConcurrentHashMap();

    public Channel bindChannel(Instance instance){

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new RestartHandlerAdapter());
            }
        });
        ChannelFuture channelFuture = null;

        try {
            channelFuture = bootstrap.connect(instance.getIp(),instance.getPort()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            work.shutdownGracefully();
        }
        if (channelFuture.isSuccess()) {


        }
        return  channelFuture.channel();



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
                socketChannel.pipeline().addLast(new NettyServerHandlerAdapter(lock));
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
            logger.info("bind client port success");
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
            logger.info("bind consumer port success");
        }

    }


    public void bindUpdateConsumeIndexPort(int port){//用于集群消费时的消费确认，下标保存在broker上

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work);

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new UpdateConsumeIndexHandler());
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
            logger.info("bind update consume index port success");
        }


    }


    public Channel bind(NameServerInfo nameServerInfo){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new RestartHandlerAdapter());
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

            logger.info("bind nameserver port success");

        }


    }


    public ConcurrentHashMap getConsumeIndexMap() {
        return ConsumeIndexMap;
    }

    public void setConsumeIndexMap(ConcurrentHashMap consumeIndexMap) {
        ConsumeIndexMap = consumeIndexMap;
    }
}
