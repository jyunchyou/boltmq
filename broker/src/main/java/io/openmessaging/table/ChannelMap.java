package io.openmessaging.table;

import java.nio.channels.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelMap {
    public static Map map = new ConcurrentHashMap<String, Channel>();

}
