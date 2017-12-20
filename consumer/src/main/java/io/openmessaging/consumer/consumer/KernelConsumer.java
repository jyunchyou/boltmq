package io.openmessaging.consumer.consumer;

import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.net.EncodeAndDecode;
import io.openmessaging.consumer.net.NettyConsumer;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import io.openmessaging.consumer.table.TopicBrokerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-7.
 */
public class KernelConsumer {

    private static KernelConsumer kernelConsumer = new KernelConsumer();

    Logger logger = LoggerFactory.getLogger(KernelConsumer.class);

    private NettyConsumer nettyConsumer = NettyConsumer.getNettyConsumer();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private KernelConsumer(){

    }

    public static KernelConsumer getKernelConsumer() {
        return kernelConsumer;
    }

    public static void setKernelConsumer(KernelConsumer kernelConsumer) {
        KernelConsumer.kernelConsumer = kernelConsumer;
    }

    public void subscribe(String topic, ListenerMessage listenerMessage, int num, CountDownLatch countDownLatch, long consumeModel){







        if (TopicBrokerTable.concurrentHashMap.isEmpty()) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        while (true) {

            pull(topic, num,listenerMessage,countDownLatch,consumeModel);

        }
    }

    public void start(final ReceiveMessageTable receiveMessageTable, final String topic){





        startTable(receiveMessageTable,topic);


        }



    public void startTable(final ReceiveMessageTable receiveMessageTable,final String topic){


            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    receiveMessageTable.updateReceiveTableFromNameServer(topic);
                }

            },0, ConstantConsumer.GET_TABLE_TIMER_PERIOD);


        }



    public void pull(String topic,int num,ListenerMessage listenerMessage,CountDownLatch countDownLatch,long uniqId){

        nettyConsumer.pull(topic,num,listenerMessage,countDownLatch,uniqId);



    }

}
