package io.openmessaging.client.net;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


import javax.swing.text.html.FormSubmitEvent;

/**
 * Created by fbhw on 17-11-25.
 */
public class NettyClientHandler extends ChannelHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){



    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext,Object msg) throws ClientException {








}}
