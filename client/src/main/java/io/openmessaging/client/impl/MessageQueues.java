package io.openmessaging.client.impl;

import io.openmessaging.client.constant.ConstantClient;
import sun.plugin2.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbhw on 17-11-5.
 */
public class MessageQueues {


    List messageQueues = new ArrayList<MessageQueue>();



    public MessageQueues() throws IOException {
    }


    public List getList(){
        if (messageQueues.size() == 0) {
            return null;
        }
        return this.messageQueues;

    }
}
