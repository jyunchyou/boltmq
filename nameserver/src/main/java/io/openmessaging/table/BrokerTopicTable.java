package io.openmessaging.table;

import io.openmessaging.producer.BrokerInfo;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerTopicTable {
    public static ConcurrentHashMap<BrokerInfo,ArrayList<String>> concurrentHashMap = new ConcurrentHashMap();
}
