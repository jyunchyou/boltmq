package io.openmessaging.consumer.consumer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.net.EncodeAndDecode;
import io.openmessaging.consumer.net.NettyConsumer;
import io.openmessaging.consumer.table.ConnectionCacheBrokerTabel;
import io.openmessaging.consumer.table.ReceiveMessageTable;
import io.openmessaging.consumer.table.TopicBrokerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by fbhw on 17-12-7.
 */
public class KernelConsumer {



    Logger logger = LoggerFactory.getLogger(KernelConsumer.class);

    private NettyConsumer nettyConsumer = new NettyConsumer();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();
    public void subscribe(String topic, ListenerMessage listenerMessage,int num){


        System.out.println("subscribe");

        if (TopicBrokerTable.concurrentHashMap.isEmpty()) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //TODO get路由from nameServer
        //TODO 建立netty连接
        //TODO 将ListenerMessage 传入 ChannelHandlerAdapter执行消息处理,在这之前进行解码

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pull(topic, num);
        }
    }

    public void start(final ReceiveMessageTable receiveMessageTable, final String topic){





        startTable(receiveMessageTable,topic);


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



    public void pull(String topic,int num){


        while (TopicBrokerTable.concurrentHashMap.isEmpty()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        /**
         * 表结构 topic-List{BrokerInfo-List{queueId}}
         *
         * */
         List<Map<BrokerInfo, List<String>>> list = TopicBrokerTable.concurrentHashMap.get(topic);


         System.out.println("preNum:1,Acture:"+list.size());

         while (list == null || list.size() == 0) {

             System.out.println("list == 0 || == null");
             try {
                 Thread.sleep(15000);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
        for (Map map : list) {
            BrokerInfo brokerInfo= (BrokerInfo) map.keySet().iterator().next();

            List queueIds = (List) map.get(brokerInfo);
            ByteBuf byteBuf = encodeAndDecode.encodePull(topic,num,queueIds);

            Channel channel = ConnectionCacheBrokerTabel.connectionCacheBrokerTable.get(brokerInfo);


            if (channel != null) {
                channel.writeAndFlush(byteBuf);

                System.out.println("yijinfasong");
            } else {


                Channel c = nettyConsumer.bind(brokerInfo);

                if (c == null) {

                    logger.info("连接失败,取消pull");

                    return ;
                }

                ConnectionCacheBrokerTabel.connectionCacheBrokerTable.put(brokerInfo,c);

                Future future = c.writeAndFlush(byteBuf);
                if (future.isSuccess()) {
                    System.out.println("-------pull请求发送-----");

                } else  {
                    System.out.println("------pull shibai-----");
                }


            }
            }





    }
}
