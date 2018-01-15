package io.openmessaging.consumer.constant;

/**
 * Created by fbhw on 17-12-7.
 */
public class ConstantConsumer {


    public static final String NAMESERVER_IP = "127.0.0.1";

    public static final int NAMESERVER_PORT = 8090;

    public static final int GET_TABLE_TIMER_PERIOD = 30000;

    public static final int PULL_BUFFER_SIZE = 1;

    public static final long GROUP_ID = 0;

    public static final int CONSUMER_PORT = 9988;

    public static final int BODY_OVER_HOW_MUTH_COMRESS = 4094;//body超过多大开始缓存,请务必保证与消费端配置一致

}
