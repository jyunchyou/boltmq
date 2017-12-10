import io.openmessaging.producer.BrokerInfo;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-12-8.
 */
public class Test {

    @org.junit.Test
    public void test(){

        ConcurrentHashMap map = new ConcurrentHashMap();

        BrokerInfo brokerInfo = new BrokerInfo();

        BrokerInfo brokerInfo2 = new BrokerInfo();

        brokerInfo.setIp("127.0.0.1");




        brokerInfo2.setIp("127.0.0.1");



        map.put(brokerInfo,"getSuccess");



    /*    Set<BrokerInfo> set = map.keySet();
        for (BrokerInfo brokerInfo1 : set) {
            if (brokerInfo1.equals(brokerInfo2)) {

                System.out.println("contain");
                return ;
            }

        }
        System.out.println("not find");*/



    }
}
