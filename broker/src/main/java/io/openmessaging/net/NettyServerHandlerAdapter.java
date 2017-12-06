package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.processor.ProcessorIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fbhw on 17-12-3.
 */
public class NettyServerHandlerAdapter extends ChannelHandlerAdapter{

    Logger logger = LoggerFactory.getLogger(NettyServerHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private ProcessorIn processIn = new ProcessorIn();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg){

        logger.info("method channelRead has executed");

        ByteBuf data = (ByteBuf) msg;
/*

        byte[] b = new byte[data.readableBytes()];

        data.readBytes(b);

        String result = new String(b);

        logger.info("Server端收到消息:"+result);
*/

        //channelHandlerContext.writeAndFlush(();
/*

        ByteBuffer byteBuffer = (ByteBuffer) msg;

        SendQueues.routeByteBuffer = byteBuffer;
*/

        List<Map> list = encodeAndDecode.decode(data);


        if (list.size() > 0) {




            for (Map map:list) {
                String topic = (String) map.get("topic");
                String queueId = (String) map.get("queueId");
                byte[] d = (byte[]) map.get("data");


                  processIn.input(d,topic,queueId);

            }
            }



        ByteBuf byteBuf = encodeAndDecode.encodeSendMessageBack();
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(byteBuf);
        if (channelFuture.isSuccess()) {
            System.out.println("返回成功");

        }else{
            System.out.println("返回失败");
        }




    }


}
