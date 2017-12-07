package io.openmessaging.consumer.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.openmessaging.consumer.consumer.BrokerInfo;
import io.openmessaging.consumer.consumer.NameServerInfo;
import io.openmessaging.consumer.table.ConnectionCacheNameServerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-7.
 */
public class NettyConsumer {

    Logger logger = LoggerFactory.getLogger(NettyConsumer.class);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private ConcurrentHashMap nameServerConnnectinoCacheTable = (ConcurrentHashMap) ConnectionCacheNameServerTable.getConnectionCacheNameServerTable();

    private Bootstrap bootstrap = null;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public NettyConsumer(){

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

    public void sendRouteRequest(Channel channel){
        ByteBuf byteBuf = Unpooled.wrappedBuffer("getTable".getBytes());
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

}
