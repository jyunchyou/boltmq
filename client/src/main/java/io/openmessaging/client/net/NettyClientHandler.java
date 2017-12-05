package io.openmessaging.client.net;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.swing.text.html.FormSubmitEvent;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fbhw on 17-11-25.
 */
public class NettyClientHandler extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    SendResult sendResult = null;

    private CountDownLatch countDownLatch = null;

    public NettyClientHandler(SendResult sendResult, CountDownLatch countDownLatch){

        this.sendResult = sendResult;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){


        System.out.println("channelActivity has executing!");

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext,Object msg) throws ClientException {

        System.out.println("message back success");
        ByteBuf byteBuf  = (ByteBuf) msg;

        byte[] resultBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(resultBytes);

        String resultString = new String(resultBytes);




        if ("1".equals(resultString)) {
            System.out.println("send message back success!");

        }else{

            logger.warn("消息发送失败");
        }

        //同步发送释放
        countDownLatch.countDown();





}}
