
package io.openmessaging.demo;

import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;
import io.openmessaging.PullConsumer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultPullConsumer implements PullConsumer {
    private MessageStore messageStore = MessageStore.getInstance();
    private KeyValue properties;
    private String queue;
    private Set<String> buckets = new HashSet<>();
    private List<String> bucketList = new ArrayList<>();
    private int threadId = 0;
    private AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    // int remain;
    public DefaultPullConsumer(KeyValue properties) {
        this.properties = properties;
    }


    @Override public KeyValue properties() {
        return properties;
    }

    @Override public  Message poll() {

        DefaultBytesMessage defaultBytesMessage = null;
try {
     defaultBytesMessage = messageStore.pullMessage(properties, threadId);
}catch (Exception e){
    System.out.println("kongzhi");
}


     /*       if (atomicBoolean.compareAndSet(true,false)) {
                      String headerKey = defaultBytesMessage.headers().keySet().iterator().next();
            String headerValue = defaultBytesMessage.headers().getString(headerKey);
            String propertiesKey = defaultBytesMessage.properties().keySet().iterator().next();
            String propertiesValue = defaultBytesMessage.properties().getString(propertiesKey);
            String body = new String(defaultBytesMessage.getBody());

                    System.out.println(headerKey+"-"+headerValue+"-"+propertiesKey+"-"+propertiesValue+"-"+new String(body));
            }*/

                return defaultBytesMessage;

            }




    @Override public Message poll(KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void ack(String messageId) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void ack(String messageId, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }
    @Override public  void attachQueue(String queueName, Collection<String> topics) {

        threadId = messageStore.getAtomicIntegerThreadId().getAndAdd(1);

        queue = queueName;

        buckets.addAll(topics);
        buckets.add(queueName);
        bucketList.clear();
        bucketList.addAll(buckets);
        messageStore.attachInit(topics,queueName,properties,threadId);


    }
}
