package io.openmessaging.client.route;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-11-6.
 */
public class RouteInfoTable {

    private ConcurrentHashMap<String/*topicName*/,RouteInfoTable> routeInfoTable = new ConcurrentHashMap();




}
