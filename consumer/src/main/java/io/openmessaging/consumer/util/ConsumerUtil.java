package io.openmessaging.consumer.util;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.openmessaging.consumer.broker.BrokerInfo;

public class ConsumerUtil {

    public static BrokerInfo cast(Instance instance){
        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setIp(instance.getIp());
        brokerInfo.setConsumerPort(instance.getPort());
        return brokerInfo;

    }

    public static String generate(String ip,int port,String group,String serviceName){
        return ip + "#" + port + "#" + group + "#" + "DEFAULT_GROUP@@" + serviceName;

    }
}
