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
    public  void testConn() throws InterruptedException {



        nettyServer.bind(8080);

        Thread.sleep(300000);


     /*   Executor executor = Executors.newFixedThreadPool(1);
        logger.info("server were start");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                nettyServer.bind(8080);


            }
        };

        executor.execute(runnable);

        try {            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


    }



}