package io.openmessaging.consumer.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.consumer.consumer.KernelConsumer;
import io.openmessaging.consumer.consumer.Message;
import io.openmessaging.consumer.listener.ListenerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbhw on 17-12-9.
 */
public class ReceiveMessageHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ReceiveMessageHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private List list = null;

    private ListenerMessage listenerMessage = null;

    private int num;

    public ReceiveMessageHandlerAdapter(int num,ListenerMessage listenerMessage){

        this.num = num;
        this.listenerMessage = listenerMessage;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;

        if (list == null) {

             list = encodeAndDecode.decodeMessage(byteBuf,num);

             if (list.size() != num) {
                 return;

             }
             listenerMessage.listener(list);
             list = null;


        }else if (list != null) {
            List l = encodeAndDecode.decodeMessage(byteBuf,num);
            int size = l.size();
            for (int indexNum = 0;indexNum < size;indexNum++) {

                list.add(l.remove(indexNum));

                if (list.size() == num) {

                    listenerMessage.listener(list);
                    if (indexNum >= size) {
                        list = null;
                    }else {
                        list = new ArrayList(num);
                    }

                }

            }

        }






        logger.info("pull message success");


    }
}