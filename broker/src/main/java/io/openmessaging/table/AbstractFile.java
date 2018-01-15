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

public class AbstractFile {

    private File file = null;

    private RandomAccessFile randomAccessFile = null;

    private MappedByteBuffer mappedByteBuffer = null;

    private FileChannel channel = null;
    public AbstractFile(String queueId,String path,int fileSize){
        init(queueId,path,fileSize);
    }

    public void init(String queueId,String fileName,int fileSize){


        file = new File(ConstantBroker.ROOT_PATH+queueId+"/"+fileName);
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
        mappedByteBuffer.put(messageByte);



    }
    public void flip(){
        mappedByteBuffer.flip();
    }


    public MappedByteBuffer getMappedByteBuffer() {
        return mappedByteBuffer;
    }
}
