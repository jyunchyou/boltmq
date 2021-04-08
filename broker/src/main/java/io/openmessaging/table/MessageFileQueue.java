package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;

import java.io.File;

public class MessageFileQueue {

    public static AbstractMessageFile abstractMessageFile;

    public static long fileWriteIndex = 0;//单个写入消息地址

    public static int queueIndex = 0;//已使用文件个数

    public static long messageIndex = 0;//消息个数下标


/*
    public MessageFileQueue(){
        File file = new File(ConstantBroker.ROOT_PATH);
        if (!file.exists()) {

                file.mkdirs();

        }
    }
*/

    public static AbstractMessage getMessage(long offset,int len){

        int queueOffset = (int) (offset/ConstantBroker.FILE_SIZE);
        int fileOffset = new Long((offset - (queueOffset * ConstantBroker.FILE_SIZE))).intValue();


        return abstractMessageFile.getMessage(fileOffset,len);

    }

    public static void putMessage(byte[] messageByte,long offset){
        int queueOffset = (int) (offset/ConstantBroker.FILE_SIZE);
        abstractMessageFile.putMessage(messageByte);

    }


}
