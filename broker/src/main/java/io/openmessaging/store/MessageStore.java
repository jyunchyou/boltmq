package io.openmessaging.store;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.Constant.ConstantBroker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by fbhw on 17-12-5.
 */
public class MessageStore {

    private static MessageStore messageStore = new MessageStore();

    private MessageStore(){

    }

    public static MessageStore getMessageStore(){
        return messageStore;
    }



    public void input(byte[] byteBuf,boolean newFile,String queueAddress,long fileAddress) throws IOException {

        File file = new File(ConstantBroker.ROOT_PATH+queueAddress+"/"+fileAddress);

        FileOutputStream fileOutputStream = null;

        FileChannel fileChannel = null;

        FileOutputStream out = null;

        FileChannel channel = null;

        try {
        if (!file.exists()) {
            file.createNewFile();

        }


           fileOutputStream = new FileOutputStream(file,true);
           fileChannel = fileOutputStream.getChannel();

           ByteBuffer byteBuffer = ByteBuffer.allocate(byteBuf.length);
           byteBuffer.put(byteBuf);
           fileChannel.write(byteBuffer);

           if (newFile) {


               long newFileName = fileAddress + ConstantBroker.FILE_SIZE;
               File f = new File(ConstantBroker.ROOT_PATH+queueAddress+"/" + newFileName);


                   if (!file.exists()) {
                       f.createNewFile();

                   }
               out = new FileOutputStream(file,true);
               channel = fileOutputStream.getChannel();


               fileChannel.write(byteBuffer);



           }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if (fileChannel != null) {
                fileChannel.close();
                }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (out != null) {
                out.close();
            }
            if (channel != null) {
                channel.close();
            }
        }



    }

    }




