package io.openmessaging.start;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.producer.BrokerInfo;
import io.openmessaging.store.IndexStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by fbhw on 17-12-17.
 */
public class BrokerRestart {




    public ByteBuf resumeData(File file){


        FileOutputStream fileOutputStream = null;
        ByteBuffer byteBuffer = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            FileChannel fileChannel = fileOutputStream.getChannel();
            byteBuffer = ByteBuffer.allocate(ConstantNameServer.INDEX_BROKER_BUFFER_SIZE);


            if (file.canRead()) {
            return null;

            }
            fileChannel.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
         }


        return Unpooled.wrappedBuffer(byteBuffer);

    }
}
