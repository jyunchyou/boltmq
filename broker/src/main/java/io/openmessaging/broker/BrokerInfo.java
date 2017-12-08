package io.openmessaging.broker;

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

    @Override
    public boolean equals(Object o){


        if (o.getClass() != this.getClass()) {


            return false;
        }
        BrokerInfo brokerInfo = (BrokerInfo) o;
        String ip = brokerInfo.getIp();
        int port = brokerInfo.getPort();
        if (!getIp().equals(ip)) {
            return false;
        }
        if (getPort() != port) {

            return false;
        }

        return true;



    }

    @Override
    public int hashCode(){

        return this.getIp().hashCode()+this.port;
    }


}
