package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AbstractMessageFile {

    private File file = null;

    private RandomAccessFile randomAccessFile = null;

    private MappedByteBuffer mappedByteBuffer = null;

    private FileChannel channel = null;

    private long index = 0;//总写入地址

    private long currentFile = 0;//当前文件

    public AbstractMessageFile(String fileName, long fileSize){
        init(fileName,fileSize);
    }

    public void init(String fileName,long fileSize){


        File root = new File(ConstantBroker.ROOT_PATH);
        if (!root.exists()) {
            root.mkdirs();
        }

        file = new File(ConstantBroker.ROOT_PATH+fileName);

        try {

            if (!file.exists()) {
                file.createNewFile();
        }




            randomAccessFile = new RandomAccessFile(file,"rw");
            channel = randomAccessFile.getChannel();
            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE,0,fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public AbstractMessage getMessage(int fileOffset,int len){


        byte[] messageByte = new byte[len];

        for (int indexNum = 0;indexNum < len;indexNum ++) {
            messageByte[indexNum] = mappedByteBuffer.get(fileOffset++);
        }


        return new AbstractMessage(messageByte);
    }

    public void putMessage(byte[] messageByte){

        long exchangeIndex = currentFile + ConstantBroker.FILE_SIZE;
        if (index + messageByte.length > exchangeIndex) {

            //换页
            file = new File(ConstantBroker.ROOT_PATH + exchangeIndex);

            try {

                if (!file.exists()) {
                    file.createNewFile();
                }




                randomAccessFile = new RandomAccessFile(file,"rw");
                channel = randomAccessFile.getChannel();
                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE,0,ConstantBroker.FILE_SIZE);
                index = exchangeIndex;
                currentFile = exchangeIndex;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        mappedByteBuffer.put(messageByte);
        mappedByteBuffer.force();
        index += messageByte.length;
    }
    public void flip(){
        mappedByteBuffer.flip();
    }



    public MappedByteBuffer getMappedByteBuffer() {
        return mappedByteBuffer;
    }

    public void position(int position){
        mappedByteBuffer.position(position);
    }
}
