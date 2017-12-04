package io.openmessaging.table;

import io.openmessaging.producer.BrokerInfo;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-4.
 */

/**
 * 表结构 brokerInfo-map{Ｎ*(topicName-queueId)}
 * N默认取4
 * 表的作用:决定了Producer的发送地址
 */
public class BrokerTopicTable {
    public static ConcurrentHashMap<BrokerInfo,HashMap<String,String>> brokerTopicTable = new ConcurrentHashMap<BrokerInfo, HashMap<String, String>>();




}
