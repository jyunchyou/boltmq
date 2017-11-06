package io.openmessaging.client.producer;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.impl.MessageImpl;
import io.openmessaging.client.impl.MessageQueue;
import io.openmessaging.client.impl.PropertiesImpl;
import io.openmessaging.client.net.ClientProcessor;
import io.openmessaging.client.net.RequestDto;
import io.openmessaging.client.net.SendResult;
import io.openmessaging.client.selector.QueueSelector;

import java.nio.ByteBuffer;

/**
 * Created by fbhw on 17-11-5.
 */
public class KernelClient {

    ClientProcessor clientProcessor = new ClientProcessor();

    public SendResult send(MessageImpl message, int delayTime, MessageQueue messageQueue, PropertiesImpl properties){

        //构建Dto
        RequestDto requestDto = new RequestDto();
        requestDto.setId(messageQueue.getQueueId());
        requestDto.setLanguage(ConstantClient.JAVA);
        requestDto.setSerialModel(ConstantClient.JSON);
        requestDto.setVersion(ConstantClient.VERSION);


        ByteBuffer byteBuffer = clientProcessor.encode(message,delayTime,properties,requestDto);

        SendResult sendResult = clientProcessor.sendSycn(byteBuffer);



        return sendResult;
    }

    public void start(String nameSvrAddress){


        clientProcessor.init(nameSvrAddress);
    }

    public void updateMessageQueuesFromNameServer(){
        clientProcessor.updateMessageQueuesFromNameServer();
    }

}
