package io.openmessaging.store;

import io.netty.buffer.ByteBuf;
import io.openmessaging.constant.ConstantNameServer;
import io.openmessaging.net.EncodeAndDecode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by fbhw on 17-12-16.
 */
public class IndexStore {

    private FileChannel fileChannel = null;

    private FileOutputStream fileOutputStream = null;

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private File file;

    private static IndexStore indexStore = new IndexStore();

    private IndexStore(){

    }


    public void save(ByteBuf byteBuf) {

        //初始化连接对象和文件句柄
        try {
            if (file == null) {

                file = new File(ConstantNameServer.INDEX_STORE_PATH);

                if (!file.exists()) {
                    file.createNewFile();
                }
            }

            if (fileChannel == null) {

                fileOutputStream = new FileOutputStream(file);

                fileChannel = fileOutputStream.getChannel();
            }




                ByteBuffer byteBuffer = byteBuf.nioBuffer();


                fileChannel.force(true);
                fileChannel.write(byteBuffer);

                
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static IndexStore getIndexStore() {
        return indexStore;
    }

}
