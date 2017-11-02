package io.openmessaging.client.net;

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

    public String processResponse(String requestDto) {

        ResponseDto responseDto = decode(requestDto);
        String common = responseDto.getCommand();
        Method method;
        try {
            method = Method.class.getMethod("sendResponse", Boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // method.invoke()


        return null;
    }


}
