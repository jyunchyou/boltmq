package io.openmessaging.client.producer;

import io.netty.channel.Channel;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.net.ClientProcessor;
import io.openmessaging.client.net.NettyClient;
import io.openmessaging.client.net.RequestDto;
import io.openmessaging.client.net.SendResult;

import java.nio.ByteBuffer;

/**
 * Created by fbhw on 17-11-5.
 */
public class KernelProducer {

    ClientProcessor clientProcessor = new ClientProcessor();
    NettyClient nettyClient = new NettyClient();

    public SendResult send(Message message, int delayTime, SendQueue sendQueue, Properties properties){

        //构建Dto
        RequestDto requestDto = new RequestDto();
        requestDto.setId(sendQueue.getQueueId());
        requestDto.setLanguage(ConstantClient.JAVA);
        requestDto.setSerialModel(ConstantClient.JSON);
        requestDto.setVersion(ConstantClient.VERSION);


        ByteBuffer byteBuffer = clientProcessor.encode(message,delayTime,properties,requestDto);

        Channel channel = nettyClient.bind(sendQueue.getBrokerInfo());

        SendResult sendResult = nettyClient.sendSycn(channel,byteBuffer);


        return sendResult;
    }

    public void start(String nameSvrAddress){
        clientProcessor.init(nameSvrAddress);
    }

    public void updateMessageQueuesFromNameServer(){
        clientProcessor.updateMessageQueuesFromNameServer();
    }

}
