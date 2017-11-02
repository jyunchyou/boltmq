package io.openmessaging.client.net;

import io.openmessaging.client.impl.MessageQueue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fbhw on 17-11-2.
 */
public class ClientProcessor {

    public RequestDto encode(String requestDto){

        return null;
    }

    public ResponseDto decode(String requestDto){

        return null;
    }

    public void processRequest(RequestDto requestDto){

    }

    //发送结束,accept到response后执行;
    public Boolean processResponse(String responseString) {

        ResponseDto responseDto = decode(responseString);
        Method method = null;
        try {
            method = MessageQueue.class.getMethod(responseDto.getCommand(),String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            return (Boolean) method.invoke(responseDto.getResult());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return null;
    }


}
