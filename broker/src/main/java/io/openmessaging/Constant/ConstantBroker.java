package io.openmessaging.Constant;

/**
 * Created by fbhw on 17-12-5.
 */
public class ConstantBroker {


    public static final String ROOT_PATH = "/home/fbhw/store/";

    public static final long FILE_SIZE = 1073741824/*1073741824*/;

    public static final int BUFFER_ROUTE_SIZE = 30000000;//路由缓冲大小

    public static final int SEND_TABLE_TIMER_PERIOD = 20000;//将表信息定时发送到nameServer的时间间隔

    public static final String BROKER_MESSAGE_IP = "127.0.0.1";

    public static final int  BROKER_MESSAGE_PORT = 8080;

    public static final int PULL_PORT = 8099;

    public static final int NAMESERVER_PORT = 9999;

    public static final int WAIT_TIME_MESSAGE = 20000;

    public static final long GROUP_ID = 0;

    public static final long DELETE_MESSAGE_INDEX_CYCLE = 864000000;//删除消息索引任务的周期执行时间

    public static final long MESSAGE_SAVE_HOW_LONG = 864000000;//消息索引保存多长时间可以删除

    public static final float DElETE_DISK_OVER = (float) 0.7;//为0到1的浮点数大小，磁盘占用大小超过多少可以释放

    public static final float DELETE_DISK_LIMIT = (float) 0.3;//为0到1的浮点数大小，每次释放磁盘控制在多少，值过小有释放掉未消费消息的风险

}
