package io.openmessaging.consumer.consumer;

import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.table.ReceiveMessageTable;

/**
 * Created by fbhw on 17-12-7.
 */
public class KernelConsumer {


    public void subscribe(String topic, ListenerMessage listenerMessage){

        //TODO get路由from nameServer
        //TODO 建立netty连接
        //TODO 将ListenerMessage 传入 ChannelHandlerAdapter执行消息处理,在这之前进行解码


    }

    public void start(final ReceiveMessageTable receiveMessageTable){


            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    receiveMessageTable.updateReceiveTableFromNameServer();
                }

            },0, ConstantConsumer.GET_TABLE_TIMER_PERIOD);

        }

}
