package io.openmessaging.client.constant;

/**
 * Created by fbhw on 17-10-31.
 */
public class ConstantClient {

    public static int CONFIRM_TIME = 30000;

    public static int SEND_CONFIRM_SIZE = 300;

    public static final int CLIENT_PORT = 9089;

    public static final int SCHEDULE_UPDATE_CHANNEL_TIME = 30;

    public static final String brokerServiceName = "broker1";

    public static final String VERSION = "0.1";

    public static final int SYNC_MODEL = 0;

    public static final int ASYNC_MODEL = 1;

    public static final int ONEWAY_MODEL = 2;

    public static final int INIT_ROUTE = 3;

    public static final long SERIR_ID = 1L;

    public static final int PROPERTIES_SIZE = 5;

    public static final String NAMESERVER_IP = "127.0.0.1";

    public static final int NAMESERVER_PORT = 8090;

    public static final int UPDATE_LIST_PORT = 12345;

    public static final int USER_BUFFER = 3000;

    public static final String JAVA = "java";

    public static final String JSON = "json";

    public static final int DELAY_TIME = 30000;//默认超时时间

    public static final int GET_LIST_TIMER_PERIOD = 60000;//周期刷新路由信息的时间间隔

    public static final int CHANNEL_TIMEOUT = 10000;//消息发送的channel等待断开的时间

    public static final int BODY_OVER_HOW_MUTH_COMRESS = 4094;//body超过多大开始缓存,请务必保证与消费端配置一致

    public static final int ASYN_MAX_NUM = 10000;

    public static final byte loadFlag = '$';//用来系统重启时，重新定位写入位置

    public static final long SEND_OUT_TIME = 300000;

}
