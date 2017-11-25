package io.openmessaging.client.producer;

import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.net.ClientProcessor;
import io.openmessaging.client.net.RequestDto;
import io.openmessaging.client.net.SendResult;

import java.nio.ByteBuffer;

/**
 * Created by fbhw on 17-11-5.
 */
public class KernelProducer {

    ClientProcessor clientProcessor = new ClientProcessor();

    public SendResult send(Message message, int delayTime, SendQueue sendQueue, Properties properties){

        //构建Dto
        RequestDto requestDto = new RequestDto();
        requestDto.setId(sendQueue.getQueueId());
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
