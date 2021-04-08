package io.openmessaging.consumer.util;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.openmessaging.consumer.broker.BrokerInfo;

public class InfoCast {

    public static BrokerInfo cast(Instance instance){
        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setIp(instance.getIp());
        brokerInfo.setConsumerPort(instance.getPort());
        return brokerInfo;

    }
}

