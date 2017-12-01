package io.openmessaging.client.producer;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    public int getSize(){
        return properties.size();
    }

    public int getAllLength(){
        int allLength = 0;

        Set entrySet = entrySet();
        Iterator<Map.Entry> iterator = entrySet.iterator();

        while (iterator.hasNext()) {

        Map.Entry<String,String> entry = iterator.next();

        allLength += entry.getKey().length();
        allLength += entry.getValue().length();

        }
        return allLength;

    }

    public Set<Map.Entry> entrySet(){
        Set set=  properties.entrySet();

        return set;
    }



}
