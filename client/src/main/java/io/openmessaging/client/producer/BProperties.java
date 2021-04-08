package io.openmessaging.client.producer;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by fbhw on 17-10-31.
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

    private HashMap properties = new HashMap(io.openmessaging.client.constant.ConstantClient.PROPERTIES_SIZE);


    public void putProperties(Object k,Object v){

        properties.put(k,v);
    }

    public Object getProperties(Object k){
        return properties.get(k);

    }

    public int getSize(){
        return properties.size();
    }

    public int getAllLength(){
        int allLength = 0;

        Set entrySet = entrySet();
        Iterator<Map.Entry> iterator = entrySet.iterator();

        while (iterator.hasNext()) {

        Map.Entry<String,String> entry = iterator.next();

        allLength += entry.getKey().getBytes().length;
        allLength += entry.getValue().getBytes().length;

        }
        return allLength;

    }

    public Set<Map.Entry> entrySet(){
        Set set=  properties.entrySet();

        return set;
    }



}
