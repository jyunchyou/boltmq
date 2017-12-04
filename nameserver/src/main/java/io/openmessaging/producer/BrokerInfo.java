package io.openmessaging.producer;

/**
 * Created by fbhw on 17-12-4.
 */
public class BrokerInfo {

    private String address = null;

    private String ip;

    private int port;

    private int readQueue = 0;

    private int writeQueue = 0;


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

    public int getReadQueue() {
        return readQueue;
    }

    public void setReadQueue(int readQueue) {
        this.readQueue = readQueue;
    }

    public int getWriteQueue() {
        return writeQueue;
    }

    public void setWriteQueue(int writeQueue) {
        this.writeQueue = writeQueue;
    }
}
