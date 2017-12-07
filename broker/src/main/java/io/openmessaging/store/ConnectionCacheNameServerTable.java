package io.openmessaging.store;

import io.netty.channel.Channel;
import io.openmessaging.nameserver.NameServerInfo;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-2.
 */
public class ConnectionCacheNameServerTable {
    private static Map<NameServerInfo,Channel> connectionCacheNameServerTable = new ConcurrentHashMap();

    public ConnectionCacheNameServerTable(){

    }

    public static Map getConnectionCacheNameServerTable(){
        return connectionCacheNameServerTable;
    }

}

