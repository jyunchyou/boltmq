package io.openmessaging.consumer.consumer;

/**
 * Created by fbhw on 17-11-6.
 */
public class BrokerInfo {

    private String address = null;

    private String ip;

    private int producerPort;

    private int nameServerPort;

    private int consumerPort;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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
}
