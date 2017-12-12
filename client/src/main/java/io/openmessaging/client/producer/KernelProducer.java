package io.openmessaging.client.producer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.openmessaging.client.common.SendCallBack;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.exception.OutOfByteBufferException;
import io.openmessaging.client.net.*;
import io.openmessaging.client.table.ConnectionCacheTable;
import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-11-5.
 */

public class KernelProducer {

   EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    NettyClient nettyClient = NettyClient.getNettyClient();

    Map<BrokerInfo,Channel> map = ConnectionCacheTable.getConnectionCacheTable();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void send(Message message, int delayTime, SendQueue sendQueue, Properties properties, SendCallBack sendCallBack,boolean oneWay) throws OutOfBodyLengthException, OutOfByteBufferException {


        BrokerInfo brokerInfo = sendQueue.getBrokerInfo();
        Channel channel = null;

        //构建Dto
        RequestDto requestDto = new RequestDto();
        requestDto.setId(sendQueue.getQueueId());//TODO 消息序号
        requestDto.setLanguage(ConstantClient.JAVA);
        requestDto.setSerialModel(ConstantClient.JSON);
        requestDto.setVersion(ConstantClient.VERSION);
        requestDto.setDelayTime(delayTime);
        requestDto.setQueueId(sendQueue.getQueueId());



        ByteBuf byteBuf = null;
        byteBuf = encodeAndDecode.encodeMessage(message,properties,requestDto);



        channel = map.get(brokerInfo);

        if (channel == null) {

            if (sendCallBack == null) {
                channel = nettyClient.bind(brokerInfo,null);
            }else {
                channel = nettyClient.bind(brokerInfo,countDownLatch);
            }
            map.put(brokerInfo, channel);

        }
        if (sendCallBack == null && oneWay == false) {

            nettyClient.send(channel, byteBuf, delayTime, null, countDownLatch);

        } else if (sendCallBack != null){
            nettyClient.send(channel,byteBuf,delayTime,sendCallBack,null);
        } else if (oneWay == true) {
            nettyClient.send(channel, byteBuf, 0, null, null);
    }

    }

    //开启定时任务
    public void start(SendQueues sendQueues){

        nettyClient.start(sendQueues);

    }
}
