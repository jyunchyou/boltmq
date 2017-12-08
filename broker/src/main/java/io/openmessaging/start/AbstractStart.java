package io.openmessaging.start;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.nameserver.NameServerInfo;
import io.openmessaging.net.NettyServer;

/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractStart {

    NettyServer nettyServer = new NettyServer();

    //开启定时任务
    public void start(NameServerInfo nameServerInfo){


        nettyServer.bind(8080);

        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                nettyServer.sendTableToNameServer(nameServerInfo);

            }

        },0, ConstantBroker.SEND_TABLE_TIMER_PERIOD);

    }

}
