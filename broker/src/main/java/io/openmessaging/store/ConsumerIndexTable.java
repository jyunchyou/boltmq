package io.openmessaging.store;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-12.
 */
//广播消费时,保存每个consumer的消费下标
public class ConsumerIndexTable {

    public static ConcurrentHashMap<String,Integer> concurrentHashMap = new ConcurrentHashMap();
}
