package io.openmessaging.store;

/**
 * Created by fbhw on 17-12-5.
 */

import io.openmessaging.Constant.ConstantBroker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据结构
 * map{queueId-FileInfoQueue}
 */
public class MessageInfoQueues {

    public static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    public MessageInfoQueues(){
        init(ConstantBroker.QUEUE_NUM);
    }
    public void init(int queueNum){
        for (int checkNum = 0;checkNum < queueNum;checkNum++) {
            MessageInfoQueue fileInfoQueue = new MessageInfoQueue(checkNum);
            List<MessageInfo> list = new ArrayList();
            concurrentHashMap.put(fileInfoQueue,list);

        }

    }

}
