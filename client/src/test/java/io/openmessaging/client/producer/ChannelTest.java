package io.openmessaging.client.producer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class ChannelTest {

    public static void main(String[] args) throws IOException {
        // 打开socket的nio管道

        List<SocketChannel> list = new ArrayList<SocketChannel>(1);

        // 将文件放到channel中

        for (int index = 0;index <1;index++) {

            SocketChannel sc = SocketChannel.open();



            sc.configureBlocking(true);

            sc.connect(new InetSocketAddress("192.168.3.106", 9026));// 绑定相应的ip和端口


            list.add(sc);



        }


        long start = System.currentTimeMillis();

        int num = 0;
                for (;;) {

if (num > 10000) {
break;
}

            for (SocketChannel socketChannel :list) {

                num++;


                socketChannel.write(ByteBuffer.wrap("testestst".getBytes()));
            }

        }
        //关闭
        //打印传输字节数

        // 打印时间
        System.out.println(
                + (System.currentTimeMillis() - start));
    }
}

