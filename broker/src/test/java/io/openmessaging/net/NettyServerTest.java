package io.openmessaging.net;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NettyServerTest{

    Logger logger = LoggerFactory.getLogger(NettyServerTest.class);
    private NettyServer nettyServer = null;

    @Before
    public void init(){
         nettyServer = new NettyServer();

    }


    @Test
    public void testConn(){
        Executor executor = Executors.newFixedThreadPool(1);
        logger.info("server were start");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                nettyServer.bind(8080);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {

                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        executor.execute(runnable);
    }



}