package io.openmessaging.client.handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.client.net.EncodeAndDecode;
import io.openmessaging.client.net.SendResult;
import io.openmessaging.client.producer.AbstractProducer;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.table.ConnectionCacheTable;
import io.openmessaging.client.table.SendQueue;
import io.openmessaging.client.table.SendQueues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-11-25.
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    SendResult sendResult = null;

    private CountDownLatch countDownLatch = null;

    private BrokerInfo brokerInfo = null;

    public SocketChannel socketChannel = null;

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    public NettyClientHandler(SocketChannel socketChannel,AbstractProducer abstractProducer){

        this.abstractProducer = abstractProducer;
        this.socketChannel = socketChannel;

    }

    private ByteBuf lastByteBuf = null;

    private AbstractProducer abstractProducer = null;

    private Channel channel = null;


    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed2");
    }

    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;


        if (lastByteBuf != null) {

            lastByteBuf.writeBytes(byteBuf);
            byteBuf = lastByteBuf;
        }


        lastByteBuf = encodeAndDecode.deCode(byteBuf,abstractProducer,socketChannel);

    }


}