package io.openmessaging.start;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.broker.NameServerInfo;
import io.openmessaging.net.NettyServer;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractStart {

    NettyServer nettyServer = new NettyServer();

    //开启定时任务
    public void start(NameServerInfo nameServerInfo){

        //接收producer发来的消息
        nettyServer.bind(ConstantBroker.BROKER_MESSAGE_PORT);
        //发送表到nameServer
        java.util.Timer timer = new java.util.Timer();//
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                nettyServer.sendTableToNameServer(nameServerInfo);

            }

        },0, ConstantBroker.SEND_TABLE_TIMER_PERIOD);

        nettyServer.bindNameServerPort(ConstantBroker.NAMESERVER_PORT);
        //接收consumer发来的请求
        nettyServer.bindPullPort(ConstantBroker.PULL_PORT);

    }

}
