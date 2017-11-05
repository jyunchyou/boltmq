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

    Socket socket= new Socket();

    Socket socketFromServer = null;

    ServerSocket serverSocket = null;

    List messageQueues = new ArrayList<MessageQueue>();

    InetSocketAddress inetSocketAddress = null;

    OutputStream outputStream = null;

    //定时更新
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//
//            updateMessageQueuesFromNameServer();
//        }
//    };

    static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public MessageQueues(String nameServerAddress){
       init(nameServerAddress);
    }

    public void init(String nameServerAddress){
        Integer port = Integer.valueOf(
                nameServerAddress.substring(
                        nameServerAddress.lastIndexOf(":"), nameServerAddress.length()
                )
        );
        String address = nameServerAddress.substring(0,nameServerAddress.lastIndexOf(":"));


        inetSocketAddress = new InetSocketAddress(address,port);
        try {
            socket.bind(inetSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            serverSocket = new ServerSocket(ConstantClient.UPDATE_LIST_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            socket.connect(inetSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //service.scheduleAtFixedRate(runnable,0,10, TimeUnit.SECONDS);

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.write("0".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


        Executors.newFixedThreadPool(1).submit(new Runnable() {
            @Override
            public void run() {
                updateMessageQueuesFromNameServer();
            }
        });

    }

    public MessageQueues() throws IOException {
    }



    //协议格式: 长度/内容
    public void updateMessageQueuesFromNameServer() {


        try {
            socketFromServer = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] len = new byte[2];

        InputStream in = null;
        try {
             in = socket.getInputStream();
             in.read(len);
             int bigNum = len[0];
             int smallNum = len[1];

             int bufferSize = 0;

             if (bigNum != 0) {
             bufferSize += bigNum * 127;
             }
             bufferSize += smallNum;
             byte[] userBuffer = new byte[bufferSize];

             in.read(userBuffer);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
