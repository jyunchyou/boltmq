package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.table.BrokerTopicTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServerHandlerAdapter extends ChannelHandlerAdapter{

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    Logger logger = LoggerFactory.getLogger(NettyServerHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg){

        logger.info("method channelRead has executed");

        ByteBuf data = (ByteBuf) msg;

        byte[] b = new byte[data.readableBytes()];

        data.readBytes(b);

        String result = new String(b);

        logger.info("Server端收到消息:"+result);

        //test
        if ("getList".equals(result)) {

            ByteBuf byteBuf = encodeAndDecode.encodeSendList();


            channelHandlerContext.writeAndFlush(byteBuf);



        } else {
            System.out.println("成功获取getTable");

            ByteBuf byteBuf = encodeAndDecode.encodeReceiveTable(result);


            ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(byteBuf);

            if (channelFuture.isSuccess()) {
                System.out.println("返回成功");

            }else {
                System.out.println("fail");
            }
        }
/*

        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;
*/

    }

}
