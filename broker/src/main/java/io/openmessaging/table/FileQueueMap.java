package io.openmessaging.table;

import java.util.concurrent.ConcurrentHashMap;

public class FileQueueMap {

    public static ConcurrentHashMap<String, MessageFileQueue> queueMap = new ConcurrentHashMap();


}
