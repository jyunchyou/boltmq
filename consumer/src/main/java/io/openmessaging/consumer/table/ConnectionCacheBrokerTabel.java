package io.openmessaging.consumer.table;

import io.netty.channel.Channel;
import io.openmessaging.consumer.broker.BrokerInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-9.
 */

public class ConnectionCacheBrokerTabel {

    public static Map<BrokerInfo,Channel> connectionCacheBrokerTable = new ConcurrentHashMap();

    public ConnectionCacheBrokerTabel(){

    }

}
