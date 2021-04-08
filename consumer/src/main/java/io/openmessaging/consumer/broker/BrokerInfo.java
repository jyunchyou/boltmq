package io.openmessaging.consumer.broker;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;

/**
 * Created by fbhw on 17-11-6.
 */
public class BrokerInfo {

    private String ip;

    private int producerPort;

    private int nameServerPort;

    private int consumerPort;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public boolean equals(Object o){


        if (o.getClass() != this.getClass()) {


            return false;
        }
        BrokerInfo brokerInfo = (BrokerInfo) o;
        String ip = brokerInfo.getIp();
        int producerPort = brokerInfo.getProducerPort();
        int nameServerPort = brokerInfo.getNameServerPort();
        int consumerPort = brokerInfo.getConsumerPort();
        if (!getIp().equals(ip)) {
            return false;
        }
        if (getProducerPort() == producerPort) {

            return true;
        }
        if (getNameServerPort() == nameServerPort) {
            return true;
        }

        if (getConsumerPort() == consumerPort) {
            return true;
        }
        return false;



    }

    @Override
    public int hashCode(){

        return this.getIp().hashCode();
    }


    public int getProducerPort() {
        return producerPort;
    }

    public void setProducerPort(int producerPort) {
        this.producerPort = producerPort;
    }

    public int getNameServerPort() {
        return nameServerPort;
    }

    public void setNameServerPort(int nameServerPort) {
        this.nameServerPort = nameServerPort;
    }

    public int getConsumerPort() {
        return consumerPort;
    }

    public void setConsumerPort(int consumerPort) {
        this.consumerPort = consumerPort;
    }


    public static void main(String[] args) throws NacosException, InterruptedException {

        NamingService naming = NamingFactory.createNamingService("192.168.3.238:8848");
        naming.registerInstance("broker2", "11.11.11.11", 8888, "brokers");

        for (;;) {
            Thread.sleep(5000);

        }
    }
}
