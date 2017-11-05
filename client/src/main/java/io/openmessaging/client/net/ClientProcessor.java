package io.openmessaging.client.net;

import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.impl.MessageImpl;
import io.openmessaging.client.impl.MessageQueue;
import io.openmessaging.client.impl.PropertiesImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by fbhw on 17-11-2.
 */
public class ClientProcessor {

    public ByteBuffer encode(MessageImpl message,int delayTime,PropertiesImpl properties,RequestDto requestDto){





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

    //TODO getBrokerAddressByBrokerName()
    public void send(ByteBuffer byteBuffer){
        //bio
        if (ConstantClient.IO_MODEL == 0) {

          /*  Socket socket = new Socket();
            socket.bind();*/
        }
        //nio
        if (ConstantClient.IO_MODEL == 1) {

        }
        //netty
        else {

        }

    }


}
