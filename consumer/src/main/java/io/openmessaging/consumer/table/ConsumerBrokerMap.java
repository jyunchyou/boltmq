package io.openmessaging.consumer.table;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerBrokerMap {
    public static Map<String, List> consumerBrokerMap = new ConcurrentHashMap();

}
