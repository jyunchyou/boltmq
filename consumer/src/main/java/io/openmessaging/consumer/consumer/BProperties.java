package io.openmessaging.consumer.consumer;

/**
 * Created by fbhw on 17-12-7.
 */
public class BProperties {
    private String server;//注册中心ip:port

    private String serviceName;//服务名称

    private String ip;

    private int port;

    private String group;//集群名称
    
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
