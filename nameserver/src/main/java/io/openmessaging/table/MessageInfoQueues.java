package io.openmessaging.table;

/**
 * Created by fbhw on 17-12-5.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据结构
 * map{queueId-InfoQueue}
 */
public class MessageInfoQueues {

    private  ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    public MessageInfoQueues(){

    }


    public ConcurrentHashMap getConcurrentHashMap() {
        return concurrentHashMap;
    }

    public void setConcurrentHashMap(ConcurrentHashMap concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }
}


