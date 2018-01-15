package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.net.EncodeAndDecode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class AbstractIndexFile {

    private File file = null;

    private RandomAccessFile randomAccessFile = null;

    private MappedByteBuffer mappedByteBuffer = null;

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private FileChannel channel = null;

    public AbstractIndexFile(String queueId,String path,int fileSize){
        init(queueId,path,fileSize);
    }

    public void init(String queueId,String fileName,int fileSize){


        file = new File(ConstantBroker.ROOT_INDEX_PATH+queueId+"/"+fileName);
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

    public AbstractIndex getIndex(int fileOffset, int len){
        byte[] indexByte = new byte[len];

        for (int indexNum = 0;indexNum < len;indexNum ++) {
            indexByte[indexNum] = mappedByteBuffer.get(fileOffset++);
        }

        System.out.println("indexByte:"+new String(indexByte));
        AbstractIndex abstractIndex = encodeAndDecode.decodeIndex(indexByte);
        return abstractIndex;
    }

    public void putIndex(byte[] indexByte){
        mappedByteBuffer.put(indexByte);




    }
    public void flip(){
        mappedByteBuffer.flip();
    }

}
