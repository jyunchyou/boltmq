package io.openmessaging.consumer.table;

/**
 * Created by fbhw on 17-12-8.
 */

import io.openmessaging.consumer.consumer.BrokerInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表结构 topic-List{BrokerInfo-List{queueId}}
 * N默认取4
 * 表的作用:决定了Producer的发送地址
 */
public class TopicBrokerTable {

    public static ConcurrentHashMap<String,List<Map<BrokerInfo,List<String>>>> concurrentHashMap = new ConcurrentHashMap();


}
