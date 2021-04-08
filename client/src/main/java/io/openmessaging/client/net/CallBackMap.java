package io.openmessaging.client.net;

import io.openmessaging.client.constant.ConstantClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class CallBackMap {

    static int maxNum = ConstantClient.ASYN_MAX_NUM;

    private static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    public static Semaphore semaphore = new Semaphore(maxNum);
    private CallBackMap(){

    }

    public static void add(String uuid,CallBackListener callBackListener) throws InterruptedException {
        if (uuid == null || callBackListener == null) {
            return;
        }
       semaphore.acquire();
       concurrentHashMap.put(uuid,callBackListener);
    }

    public static void release(String uuid){
        if (uuid == null) {
            return;
        }
        semaphore.release();
        concurrentHashMap.remove(uuid);
    }

}
