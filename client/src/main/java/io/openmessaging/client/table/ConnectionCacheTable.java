package io.openmessaging.client.table;

import io.netty.channel.Channel;
import io.openmessaging.client.producer.BrokerInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-11-8.
 */
public class ConnectionCacheTable {

    private static Map<BrokerInfo,Channel> connectionCacheTable = new ConcurrentHashMap();

    private ConnectionCacheTable(){

    }

    public static Map getConnectionCacheTable(){
        return connectionCacheTable;
    }

}
