package io.openmessaging.client.net;

import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.impl.MessageImpl;
import io.openmessaging.client.impl.MessageQueue;
import io.openmessaging.client.impl.PropertiesImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by fbhw on 17-11-2.
 */
public class ClientProcessor {

    Socket socket= new Socket();

    Socket socketFromServer = null;

    ServerSocket serverSocket = null;

    InetSocketAddress inetSocketAddress = null;

    OutputStream outputStream = null;

    InputStream in = null;
//TODO 区分route和message的Socket

    //定时更新
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//
//            updateMessageQueuesFromNameServer();
//        }
//    };

    static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public void  init(String nameServerAddress){
        Integer port = Integer.valueOf(
                nameServerAddress.substring(
                        nameServerAddress.lastIndexOf(":"), nameServerAddress.length()
                )
        );
        String address = nameServerAddress.substring(0,nameServerAddress.lastIndexOf(":"));


        inetSocketAddress = new InetSocketAddress(address,port);


        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(inetSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Executors.newFixedThreadPool(1).submit(new Runnable() {
            @Override
            public void run() {

                try {
                    socketFromServer = serverSocket.accept();

                    in = socketFromServer.getInputStream();

                    socket.bind(inetSocketAddress);

                    socket.connect(inetSocketAddress);

                    //service.scheduleAtFixedRate(runnable,0,10, TimeUnit.SECONDS);

                    outputStream = socket.getOutputStream();

                    sendSycn(ByteBuffer.allocate(1).put("0".getBytes()));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) {

                    updateMessageQueuesFromNameServer();

                }
            }
        });

    }
    //协议格式: 长度/内容
    //持久连接,从io流开始对应不同的解码方式
    public void updateMessageQueuesFromNameServer(){
        //TODO　 了解broker,queue,Topic的关系,定制协议,对userBuffer解码
        byte[] userBuffer = null;
        try {

            byte[] len = new byte[2];
            in.read(len);
            int bigNum = len[0];
            int smallNum = len[1];

            int bufferSize = 0;

            if (bigNum != 0) {
                bufferSize += bigNum * 127;
            }
            bufferSize += smallNum;
            userBuffer = new byte[bufferSize];

            in.read(userBuffer);


        } catch (IOException e) {
            e.printStackTrace();
        }
        decode(userBuffer);
        }

    public void decode(byte[] userBuffer){
        //TODO 把userBuffer解析为RouteInfoTable


    }
   /* -------------------------------以上为route表消息初始化和定时更新---------------------------------------*/
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
   /* -------------------------------下面为消息发送--------------------------------------------------------*/

    public ByteBuffer encode(MessageImpl message,int delayTime,PropertiesImpl properties,RequestDto requestDto){

        return null;
    }

    public ResponseDto decode(String requestDto){

        return null;
    }

    public void processRequest(RequestDto requestDto){

    }
    /*//发送结束,accept到response后执行;
    public Boolean processResponse(String responseString) {

        ResponseDto responseDto = decode(responseString);
        Method method = null;
        try {
            method = MessageQueue.class.getMethod(responseDto.getCommand(),String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            return (Boolean) method.invoke(responseDto.getResult());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return null;
    }
*/
    //TODO getBrokerAddressByBrokerName() and find connection with broker,queue and topic
    public SendResult sendSycn(ByteBuffer byteBuffer) {
        //bio
        if (ConstantClient.IO_MODEL == 0) {
            //消息模式orRoute更新
                try {


                    outputStream.write(byteBuffer.array());

                    InputStream inputStream = null;

                    inputStream = socket.getInputStream();

                    byte[] userBuffer = null;

                    byte[] len = new byte[2];

                    inputStream.read(len);

                    int bigNum = len[0];

                    int smallNum = len[1];

                    int bufferSize = 0;

                    if (bigNum != 0) {
                        bufferSize += bigNum * 127;
                    }
                    bufferSize += smallNum;
                    userBuffer = new byte[bufferSize];

                    inputStream.read(userBuffer);

                    SendResult sendResult = new SendResult();

                    sendResult.decode(userBuffer);

                    return sendResult;

                } catch (IOException e) {
                    e.printStackTrace();
                }
          /*  Socket socket = new Socket();
            socket.bind();*/

            //nio
            if (ConstantClient.IO_MODEL == 1) {

                return null;
            }
            //netty
            else {

                return null;
            }

        }

        return null;

    }}
