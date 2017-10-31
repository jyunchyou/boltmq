package io.openmessaging.client.common;

import io.openmessaging.client.constant.ConstantProducer;

import java.util.HashMap;

/**
 * Created by fbhw on 17-10-31.
 */
public class DefaultProperties<K,V>{

    private HashMap properties = new HashMap(ConstantProducer.PROPERTIES_SIZE);


    public void putProperties(K k,V v){

        properties.put(k,v);
    }



}
