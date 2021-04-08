package io.openmessaging.client.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class ChannelTest2 {

    public static void main(String[] args) throws IOException {
        // 创建socket channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket ss = serverSocketChannel.socket();
        ss.setReuseAddress(true);// 地址重用
        ss.bind(new InetSocketAddress("192.168.3.106", 9026));// 绑定地址
        System.out.println("监听端口 : "
                + new InetSocketAddress("192.168.3.106", 9026).toString());

        final List<SocketChannel> list = new ArrayList();
        // 分配一个新的字节缓冲区
        final ByteBuffer dst = ByteBuffer.allocate(4096);
        // 读取数据

        new Thread(new Runnable() {
            public void run() {
                while (list.size() != 0) {

                    for (SocketChannel channle :list) {
                        try {
                            channle.configureBlocking(true);// 设置阻塞，接不到就停
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int nread = 0;
                        while (nread != -1) {
                            try {
                                nread = channle.read(dst);// 往缓冲区里读
                                byte[] array = dst.array();//将数据转换为array
                                //打印
                                String string = new String(array, 0, dst.position());
                                System.out.print(string);
                                dst.clear();
                            } catch (IOException e) {
                                e.printStackTrace();
                                nread = -1;
                            }
                        }
                    }
                }
            }
        }).start();
        while (true) {
            SocketChannel channle = serverSocketChannel.accept();// 接收数据

            list.add(channle);

        }
    }}
