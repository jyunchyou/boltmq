package io.openmessaging.client.net;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.producer.FactoryProducer;
import io.openmessaging.client.producer.NameServerInfo;
import io.openmessaging.client.table.ConnectionCacheNameServerTable;
import io.openmessaging.client.table.ConnectionCacheTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
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

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    public NettyClient(){

    }
    public  Channel bind(BrokerInfo brokerInfo){
        Channel channel = null;
        if (( channel = connectionCacheTable.get(brokerInfo)) != null) {

            return channel;

        }


        String ip = brokerInfo.getIp();
        int port = brokerInfo.getPort();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.remoteAddress(ip,port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {

                    socketChannel.pipeline().addLast(new NettyClientHandler());
                }
            });
            ChannelFuture future = bootstrap.connect(ip,port).sync();
            channel = future.channel();

            if (future.isSuccess()) {

                socketChannel = (SocketChannel) future.channel();

                logger.info("client connect server success");

            }



        }catch (Exception e){

        }finally {
            eventLoopGroup.shutdownGracefully();
        }
        return channel;
    }

    public SendResult sendSycn(Channel channel,ByteBuffer byteBuffer){
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        ChannelFuture channelFuture = channel.write(byteBuf);
        SendResult sendResult = new SendResult();

        if (channelFuture.isSuccess()){

            String info = "向Broker发送成功";
            logger.info(info);
            sendResult.setInfo(info);
            return sendResult;

        }
        String info = "向Broker发送失败";
        sendResult.setInfo(info);
        return sendResult;
    }

    //nameServer
    public  Channel bind(NameServerInfo nameServerInfo){
        Channel channel = null;
        if (( channel = nameServerConnectionCacheTable.get(nameServerInfo)) != null) {

            return channel;

        }
        System.out.println("缓存table中channel 为 null");
        String ip = nameServerInfo.getIp();
        int port = nameServerInfo.getPort();


        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.remoteAddress(ip,port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {

                    socketChannel.pipeline().addLast(new UpdateFromNameServerHandler(countDownLatch));
                }
            });
            ChannelFuture future = bootstrap.connect(ip,port).sync();

            if (future.isSuccess()) {


                channel = (SocketChannel) future.channel();

                logger.info("client connect server success");

            }else {
                logger.info("client connect server fail");
            }



        }catch (Exception e){

            e.printStackTrace();
            logger.error(e.getMessage());
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
        return channel;
    }

    public void sendRouteRequest(Channel channel){

        ByteBuf byteBuf = Unpooled.wrappedBuffer("getList".getBytes());
        ChannelFuture channelFuture = channel.write(byteBuf);

        if (channelFuture.isSuccess()){

            String info = "向nameServer发送成功";
            logger.info(info);

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }



        }else {
            logger.info("向nameServer发送失败");
        }

        return ;

    }






}
