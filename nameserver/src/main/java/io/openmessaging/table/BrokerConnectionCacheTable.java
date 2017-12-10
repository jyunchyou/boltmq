package io.openmessaging.table;

import io.openmessaging.producer.BrokerInfo;

import java.nio.channels.Channel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-10.
 */
public class BrokerConnectionCacheTable {

    public static ConcurrentHashMap<BrokerInfo,Channel> concurrentHashMap = new ConcurrentHashMap();
}
