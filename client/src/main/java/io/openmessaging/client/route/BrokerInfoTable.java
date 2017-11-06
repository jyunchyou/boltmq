package io.openmessaging.client.route;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-11-6.
 */
public class BrokerInfoTable {

    private ConcurrentHashMap<String/*brokerName*/,BrokerInfo> brokerInfoTable = new ConcurrentHashMap();

}
