package io.openmessaging.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.store.IndexStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-7.
 */
public class BrokerTableHandlerAdapter extends ChannelHandlerAdapter {

    private IndexStore indexStore = IndexStore.getIndexStore();

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    Logger logger = LoggerFactory.getLogger(BrokerTableHandlerAdapter.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("activity");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;

        byteBuf.markReaderIndex();
        Object o = encodeAndDecode.decode(byteBuf);

        if (o == null) {
            return ;
        }
        //如果返回类型不为String,则返回输出brokerIndex数据
        if (!String.class.equals(o.getClass())) {
            ByteBuf brokerIndex = (ByteBuf) o;
            channelHandlerContext.writeAndFlush(brokerIndex);

            System.out.println("没有保存索引");
            return ;
        }

        byteBuf.resetReaderIndex();
        indexStore.save(byteBuf,(String) o);
        //TODO 保存了Broker消息索引，但是没有保存borker的消费下标
        //然后重启时条件判断是否为重启，返回消息索引和消费下标

    }
    }

