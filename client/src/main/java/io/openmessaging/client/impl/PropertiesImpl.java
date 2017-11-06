package io.openmessaging.client.impl;



import java.util.HashMap;

/**
 * Created by fbhw on 17-10-31.
 */
public class PropertiesImpl{

    private HashMap properties = new HashMap(io.openmessaging.client.constant.ConstantClient.PROPERTIES_SIZE);


    public void putProperties(Object k,Object v){

        properties.put(k,v);
    }

    public Object getProperties(Object k){
        return properties.get(k);

    }



}
