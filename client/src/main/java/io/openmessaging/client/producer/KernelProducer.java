package io.openmessaging.client.producer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.exception.OutOfByteBufferException;
import io.openmessaging.client.net.*;
import io.openmessaging.client.table.ConnectionCacheTable;
import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;

import java.util.Map;

/**
 * Created by fbhw on 17-11-5.
 */

public class KernelProducer {

   EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    NettyClient nettyClient = new NettyClient();

    Map<BrokerInfo,Channel> map = ConnectionCacheTable.getConnectionCacheTable();

    public void send(Message message, int delayTime, SendQueue sendQueue, Properties properties) throws OutOfBodyLengthException, OutOfByteBufferException {


        BrokerInfo brokerInfo = sendQueue.getBrokerInfo();
        Channel channel = null;

        //构建Dto
        RequestDto requestDto = new RequestDto();
        requestDto.setId(sendQueue.getQueueId());
        requestDto.setLanguage(ConstantClient.JAVA);
        requestDto.setSerialModel(ConstantClient.JSON);
        requestDto.setVersion(ConstantClient.VERSION);
        requestDto.setDelayTime(delayTime);
        requestDto.setQueueId(sendQueue.getQueueId());


        ByteBuf byteBuf = null;
        byteBuf = encodeAndDecode.encodeMessage(message,properties,requestDto);


        channel = map.get(brokerInfo);

        if (channel == null) {

            channel = nettyClient.bind(brokerInfo);

            map.put(brokerInfo, channel);

        }
        nettyClient.sendSycn(channel,byteBuf);



    }

    //开启定时任务
    public void start(SendQueues sendQueues){

        nettyClient.start(sendQueues);

    }
}
