package io.openmessaging.table;

/**
 * Created by fbhw on 17-12-5.
 */

import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据结构
 * map{topic-FileInfoQueue}
 */

public class MessageInfoQueues {

    public static ConcurrentHashMap<String,MessageInfoQueue> concurrentHashMap = new ConcurrentHashMap();

    private MessageInfoQueues(){
        /*init(ConstantBroker.QUEUE_NUM);*/
    }

    public static void init(int queueNum){
    }

    public static ConcurrentHashMap<String, MessageInfoQueue> get(){
        return concurrentHashMap;
    }
}
