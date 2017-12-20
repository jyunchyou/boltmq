package io.openmessaging.constant;

/**
 * Created by fbhw on 17-12-4.
 */
public class ConstantNameServer {

    public static final int QUEUE_NUM = 4;//一个broker上面的queue(topic)个数,自动分配4个

    public static final int ROUTE_TABLE_BUFFER_SIZE = 10000;

    public static final int NAMESERVER_PORT = 8090;

    public static final int RECEIVE_FROM_BROKER_PORT = 8088;

    public static final int UPDATE_TOPIC_PORT = 8899;

    public static final String INDEX_STORE_PATH = "/home/fbhw/store/";

    public static final int INDEX_BROKER_BUFFER_SIZE = 10000;

    public static final int CHANNEL_TIMEOUT = 10000;

    public static final int BUFFER_ROUTE_SIZE = 30000000;//路由缓冲大小




}
