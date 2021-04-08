package io.openmessaging.client.util;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.table.SendQueue;

public class InfoCast {

    public static SendQueue cast(Instance instance){
        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setIp(instance.getIp());
        brokerInfo.setConsumerPort(instance.getPort());
        SendQueue sendQueue = new SendQueue();
        sendQueue.setBrokerInfo(brokerInfo);
        //sendQueue
        return sendQueue;

    }
}

