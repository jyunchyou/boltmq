package io.openmessaging.demo;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 17-5-24.
 */
public class QueueProxy {

    private Queue<DefaultBytesMessage> queue = null;
    private Map<Integer,LinkedList<DefaultBytesMessage>> hashMap = new HashMap();
    private AtomicBoolean queueFlag = new AtomicBoolean(false);
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private AtomicInteger atomicIntegerThread = new AtomicInteger(0);
    private ThreadLocal<Integer> threadLocal = new ThreadLocal();
    public void add(DefaultBytesMessage defaultBytesMessage){
        if (queueFlag.get() == true) {
            queue.add(defaultBytesMessage);

        }
         Integer threadId= threadLocal.get();

        if (threadId == null) {
            int id = atomicIntegerThread.getAndAdd(1);
            threadLocal.set(id);
        }

         }
    public DefaultBytesMessage poll(){
        if (queueFlag.get() == true) {
            return queue.poll();
        }

        return null;
    }
    public int size()
    {
        if (queueFlag.get() == true)
        return queue.size();

        return 0;
    }

    public void attachQueue(){
        queueFlag.compareAndSet(false,true);
        queue = new LinkedList<DefaultBytesMessage>();



    }
    public void attachTopics(){


        hashMap.put(atomicInteger.getAndAdd(1),new LinkedList<DefaultBytesMessage>());



    }
}
