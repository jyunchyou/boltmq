package io.openmessaging.Constant;

/**
 * Created by fbhw on 17-12-5.
 */
public class ConstantBroker {


    public static final String ROOT_PATH = "/home/fbhw/store/";

    public static final int QUEUE_NUM = 4;//默认4个

    public static final long FILE_SIZE = 100000/*1073741824*/;

    public static final int BUFFER_ROUTE_SIZE = 3000;//路由缓冲大小

    public static final int SEND_TABLE_TIMER_PERIOD = 20000;//将表信息定时发送到nameServer的时间间隔


    public static final String BROKER_MESSAGE_IP = "127.0.0.1";

    public static final int  BROKER_MESSAGE_PORT = 8080;

    public static final int PULL_PORT = 8099;

    public static final int NAMESERVER_PORT = 9999;

    public static final int WAIT_TIME_MESSAGE = 10000;
}
