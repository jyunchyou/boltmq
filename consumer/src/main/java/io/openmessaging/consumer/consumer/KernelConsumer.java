package io.openmessaging.consumer.consumer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.net.EncodeAndDecode;
import io.openmessaging.consumer.net.NettyConsumer;
import io.openmessaging.consumer.table.ConnectionCacheBrokerTabel;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import io.openmessaging.consumer.table.TopicBrokerTable;

import java.util.List;
import java.util.Map;

/**
 * Created by fbhw on 17-12-7.
 */
public class KernelConsumer {



    private NettyConsumer nettyConsumer = new NettyConsumer();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();
    public void subscribe(String topic, ListenerMessage listenerMessage){

        //TODO get路由from nameServer
        //TODO 建立netty连接
        //TODO 将ListenerMessage 传入 ChannelHandlerAdapter执行消息处理,在这之前进行解码


    }

    public void start(ReceiveMessageTable receiveMessageTable,String topic){

        startTable(receiveMessageTable,topic);

        pull(topic);


        }



    public void startTable(final ReceiveMessageTable receiveMessageTable,final String topic){


            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    receiveMessageTable.updateReceiveTableFromNameServer(topic);
                }

            },0, ConstantConsumer.GET_TABLE_TIMER_PERIOD);


        }



    public void pull(String topic){

        while (TopicBrokerTable.concurrentHashMap.size() == 0) {
            continue;

        }

        ByteBuf byteBuf = encodeAndDecode.encodePull();

        /**
         * 表结构 topic-List{BrokerInfo-List{queueId}}
         *
         * */
         List<Map<BrokerInfo, List<String>>> list = TopicBrokerTable.concurrentHashMap.get(topic);

        for (Map map : list) {
            BrokerInfo brokerInfo= (BrokerInfo) map.keySet().iterator().next();

            Channel channel = ConnectionCacheBrokerTabel.connectionCacheBrokerTable.get(brokerInfo);


            if (channel != null) {
                channel.writeAndFlush(byteBuf);
            } else {


                Channel c = nettyConsumer.bind(brokerInfo);

                ConnectionCacheBrokerTabel.connectionCacheBrokerTable.put(brokerInfo,c);

                c.writeAndFlush(byteBuf);

            }
            }





    }
}
