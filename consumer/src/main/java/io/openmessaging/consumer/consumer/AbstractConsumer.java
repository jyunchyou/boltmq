package io.openmessaging.consumer.consumer;


import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractConsumer {

    Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);

    private Properties implProperties = null;

    private KernelConsumer kernelConsumer = new KernelConsumer();

    private ReceiveMessageTable receiveMessageTable = new ReceiveMessageTable();

    private String topic = null;

    public AbstractConsumer(){

    }

    public void subscribe(String topic, ListenerMessage listenerMessage){

        this.topic = topic;
        kernelConsumer.subscribe(topic,listenerMessage);

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
