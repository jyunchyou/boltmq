package io.openmessaging.table;

import java.util.concurrent.ConcurrentHashMap;

public class IndexFileQueueMap {
    public static ConcurrentHashMap<String,IndexFileQueue> indexQueueMap = new ConcurrentHashMap();


}
