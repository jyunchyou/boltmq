package io.openmessaging.client.process;

import io.netty.channel.Channel;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.net.BaseMessage;
import io.openmessaging.client.net.EncodeAndDecode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessDelayAck implements Runnable{

    public EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    public ConcurrentHashMap<Channel, Map> delayAckMap = new ConcurrentHashMap<Channel, Map>();
    //delayAckMap里的key为发送端channel,map里的channel为接收端
    @Override
    public void run() {
        for (; ; ) {

            if (delayAckMap == null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            for (Map.Entry<Channel, Map> entry : delayAckMap.entrySet()) {

                Map<Channel, Map> m = entry.getValue();
                for (Map.Entry<Channel, Map> en : m.entrySet()) {

                    Map<Integer, BaseMessage> map = (Map) en.getValue();
                    for (Map.Entry e : map.entrySet()) {
                        BaseMessage baseMessage = (BaseMessage) e.getValue();

                        if (baseMessage == null) {

                        }
                        if (System.currentTimeMillis() - baseMessage.getSendTimeStamp() > ConstantClient.SEND_OUT_TIME) {
                            //超时重传
                            Channel channel = (Channel) entry.getKey();
                          /*  try {
                                channel.writeAndFlush(encodeAndDecode.encodeBaseMessage2(baseMessage, (Long) e.getKey()));
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                                continue;
                            }*/

                            baseMessage.setSendTimeStamp(System.currentTimeMillis());


                        }
                    }
                }
            }
        }
    }
}
