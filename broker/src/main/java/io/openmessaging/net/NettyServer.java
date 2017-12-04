package io.openmessaging.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServer {

    private EventLoopGroup work = new NioEventLoopGroup();

    private EventLoopGroup boss = new NioEventLoopGroup();

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



}
