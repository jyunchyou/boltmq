package io.openmessaging.consumer.consumer;

/**
 * Created by fbhw on 17-11-6.
 */
public class BrokerInfo {

    private String address = null;

    private String ip;

    private int port;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
