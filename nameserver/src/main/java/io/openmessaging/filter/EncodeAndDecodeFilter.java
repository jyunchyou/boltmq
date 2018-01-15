package io.openmessaging.filter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EncodeAndDecodeFilter {

    public Map filterBrokerTopicTable(Map map){
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        Set<Map.Entry> set = (Set<Map.Entry>) map.entrySet();

        for (Map.Entry s : set) {
            ArrayList list = (ArrayList) s.getValue();
            if (list == null || list.size() == 0) {
                continue;
            }
            concurrentHashMap.put(s.getKey(),s.getValue());
        }
        return concurrentHashMap;

    }
}
