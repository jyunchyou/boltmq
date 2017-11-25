package io.openmessaging.client.table;

import io.openmessaging.client.producer.BrokerInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-11-6.
 */
public class BrokerInfoTable {

    private ConcurrentHashMap<String/*brokerName*/,BrokerInfo> brokerInfoTable = new ConcurrentHashMap();

}
