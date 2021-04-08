package io.openmessaging.consumer.table;

import io.netty.channel.Channel;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.consumer.*;
import io.openmessaging.consumer.exception.RegisterException;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.net.NettyConsumer;
import io.openmessaging.consumer.util.ConsumerUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

/**
 * Created by fbhw on 17-12-7.
 */
public class TestReceiveMessageTable {

    //Logger logger = LoggerFactory.getLogger(TestReceiveMessageTable.class);

    //private ReceiveMessageTable receiveMessageTable = null;

    //private AbstractConsumer abstractConsumer = null;

    public static void main(String[] args) throws RegisterException, Exception {
        BProperties BProperties = new BProperties();
        BProperties.setIp("127.0.0.1");
        BProperties.setPort(7789);//服务器的
        BProperties.setGroup("consumers");
        BProperties.setServer("127.0.0.1:8848");
        BProperties.setServiceName(ConstantConsumer.CONSUME_SERVICE_NAME);
        FactoryConsumer.registry(BProperties);
        String instanceId = ConsumerUtil.generate(BProperties.getIp(),BProperties.getPort(),BProperties.getGroup(),BProperties.getServiceName());



        AbstractConsumer abstractConsumer = FactoryConsumer.createConsumer("hhjjqq",instanceId,new ConsumeCallBack(1),ConstantConsumer.CLUSTER_MODEL);
        for (;;) {
            abstractConsumer.pull(instanceId);
        }
    }

    //@Test
//    public void testUpdateReceiveTableFromNameServer() throws InterruptedException {
//
//
//
//
//        new Thread(new Runnable() {
//            public void run() {
//
//
//                try {
//                    abstractConsumer.subscribe("TOPIC_01", new ListenerMessage() {
//                        public void listener(Message message) {
//
//                        }
//
//                        public void listener(List<Message> list) {
//
//                        }
//
//
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } catch (RegisterException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
//
////        abstractConsumer.start();//启动定时任务
//
//
//
//
//
//
//
//
//
//
//        Thread.sleep(10000000);
//
//    }




}
