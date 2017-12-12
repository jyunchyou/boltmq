package io.openmessaging.consumer.consumer;

import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractConsumer {

    Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);

    private Properties implProperties = null;

    private KernelConsumer kernelConsumer = KernelConsumer.getKernelConsumer();

    private ReceiveMessageTable receiveMessageTable = new ReceiveMessageTable();

    private String topic = null;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public AbstractConsumer(){

    }



    public void subscribe(String topic, ListenerMessage listenerMessage){

        subscribe(topic,listenerMessage, ConstantConsumer.PULL_BUFFER_SIZE);

    }

    public void subscribe(String topic, ListenerMessage listenerMessage,int num){
        this.topic = topic;
        kernelConsumer.subscribe(topic,listenerMessage,num,countDownLatch);

    }
    //开启定时任务
    public void start(){

        if (topic == null) {
            throw new NullPointerException();

        }
        kernelConsumer.start(receiveMessageTable,topic);
    }


    public Properties getImplProperties() {
        return implProperties;
    }

    public void setImplProperties(Properties implProperties) {
        this.implProperties = implProperties;
    }
}
