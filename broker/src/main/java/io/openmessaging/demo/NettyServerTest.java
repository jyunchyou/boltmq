package io.openmessaging.demo;

import io.openmessaging.net.NettyServer;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServerTest {

    public static void main(String[] args){
        NettyServer nettyServer = new NettyServer();
        nettyServer.bind(8080);

    }
}
