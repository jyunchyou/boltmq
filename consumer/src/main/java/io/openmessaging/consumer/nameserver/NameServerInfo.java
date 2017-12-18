package io.openmessaging.consumer.nameserver;

/**
 * Created by fbhw on 17-12-2.
 */
public class NameServerInfo {

    private String address = null;

    private String ip;

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
