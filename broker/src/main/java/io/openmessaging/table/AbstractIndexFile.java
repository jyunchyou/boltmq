package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.net.EncodeAndDecode;

import java.io.*;
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

    public AbstractIndexFile(String topic,String fileName,long fileSize){
        init(topic,fileName,fileSize);
    }

    public void init(String topic,String fileName,long fileSize){


        file = new File(ConstantBroker.ROOT_INDEX_PATH+topic+"\\"+fileName);
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

        AbstractIndex abstractIndex = encodeAndDecode.decodeIndex(indexByte);
        return abstractIndex;
    }

    public void putIndex(byte[] indexByte){
        mappedByteBuffer.put(indexByte);

        mappedByteBuffer.force();



    }

    public void putIndex(long l1,int l2,long l3){

        mappedByteBuffer.putLong(l1);
        mappedByteBuffer.putInt(l2);
        mappedByteBuffer.putLong(l3);
        mappedByteBuffer.put(ConstantBroker.loadFlag);
        mappedByteBuffer.force();


    }

    public void position(int position){
        mappedByteBuffer.position(position);
    }


    public void flip(){
        mappedByteBuffer.flip();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

   /*     RandomAccessFile randomAccessFile = new RandomAccessFile(new File("D:\\cjnf\\test.txt"),"rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0,1073741824);







        System.out.println(mappedByteBuffer.remaining());
        System.out.println(mappedByteBuffer.hasRemaining());
        mappedByteBuffer.put("777".getBytes(),0,2);

        System.out.println(mappedByteBuffer.remaining());

        mappedByteBuffer.put("888".getBytes(),3,6);
        byte[] b = new byte[200];

        mappedByteBuffer.position(0);
        mappedByteBuffer.get(b);
        System.out.println(new String(b));



        //mappedByteBuffer.put("&&&".getBytes());

        mappedByteBuffer.force();


        Thread.sleep(60000);*/
        }

}
