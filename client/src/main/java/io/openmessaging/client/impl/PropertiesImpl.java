package io.openmessaging.client.impl;



import java.util.HashMap;

/**
 * Created by fbhw on 17-10-31.
 */
public class PropertiesImpl<K,V>{

    private HashMap properties = new HashMap(io.openmessaging.client.constant.ConstantClient.PROPERTIES_SIZE);


    public void putProperties(K k,V v){

        properties.put(k,v);
    }



}
