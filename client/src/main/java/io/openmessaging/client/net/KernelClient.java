package io.openmessaging.client.net;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.impl.MessageImpl;
import io.openmessaging.client.impl.MessageQueue;
import io.openmessaging.client.impl.PropertiesImpl;
import io.openmessaging.client.selector.QueueSelector;

import java.nio.ByteBuffer;

/**
 * Created by fbhw on 17-11-5.
 */
public class KernelClient {

    ClientProcessor clientProcessor = new ClientProcessor();

    public SendResult send(MessageImpl message, int delayTime, MessageQueue messageQueue,PropertiesImpl properties){

        //构建Dto
        RequestDto requestDto = new RequestDto();
        requestDto.setId(messageQueue.getQueueId());
        requestDto.setLanguage(ConstantClient.JAVA);
        requestDto.setSerialModel(ConstantClient.JSON);
        requestDto.setVersion(ConstantClient.VERSION);


        ByteBuffer byteBuffer = clientProcessor.encode(message,delayTime,properties,requestDto);

        


        return new SendResult();
    }
}
