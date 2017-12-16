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
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-12-9.
 */
public class ReceiveMessageHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ReceiveMessageHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private List list;

    private ListenerMessage listenerMessage = null;

    private int num;

    private CountDownLatch countDownLatch = null;

    public ReceiveMessageHandlerAdapter(int num, ListenerMessage listenerMessage, CountDownLatch countDownLatch){

        this.num = num;
        this.listenerMessage = listenerMessage;
        this.countDownLatch = countDownLatch;
        this.list = new ArrayList(num);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {


        ByteBuf byteBuf = (ByteBuf) msg;

        if (byteBuf.readableBytes() <= 0) {
            countDownLatch.countDown();
            return;
        }

//decode缓存byte[],为一级解析
// channelhandleradapter缓存 list,为二级解析,只返回list 没有大小限制,可能小于pullNum
// 也可能等于,也可能大于


        List backList = encodeAndDecode.decodeMessage(byteBuf,num);

        if (backList.size() == 0) {
            countDownLatch.countDown();
            return;
        }else {
            for (int checkNum = 0;checkNum < backList.size();checkNum++) {

                list.add(backList.remove(checkNum));

                if (list.size() == num) {
                    listenerMessage.listener(list);
                    list = new ArrayList(num);
                }
            }
            countDownLatch.countDown();
            }
    }
}