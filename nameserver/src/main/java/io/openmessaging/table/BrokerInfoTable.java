package io.openmessaging.table;

import io.openmessaging.producer.BrokerInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-8.
 */


public class BrokerInfoTable {


    public static ConcurrentHashMap<BrokerInfo,MessageInfoQueues> map = new ConcurrentHashMap();



}
