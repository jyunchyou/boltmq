package io.openmessaging.client.producer;



import java.util.HashMap;

/**
 * Created by fbhw on 17-10-31.
 */
public class Properties {

    private HashMap properties = new HashMap(io.openmessaging.client.constant.ConstantClient.PROPERTIES_SIZE);


    public void putProperties(Object k,Object v){

        properties.put(k,v);
    }

    public Object getProperties(Object k){
        return properties.get(k);

    }



}
