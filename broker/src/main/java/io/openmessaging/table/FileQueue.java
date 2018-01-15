package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileQueue {

    private CopyOnWriteArrayList<AbstractFile> fileQueue = new CopyOnWriteArrayList<AbstractFile>();

    private long fileWriteIndex = 0;

    private int queueIndex = 0;


    public FileQueue(String topic){
        File file = new File(ConstantBroker.ROOT_PATH+topic);
        if (!file.exists()) {

                file.mkdir();

        }
    }

    public AbstractMessage getMessage(long offset,int len){

        int queueOffset = (int) (offset/ConstantBroker.FILE_SIZE);
        int fileOffset = new Long((offset - (queueOffset * ConstantBroker.FILE_SIZE))).intValue();


        return fileQueue.get(queueOffset).getMessage(fileOffset,len);

    }

    public void putMessage(byte[] messageByte,long offset){
        int queueOffset = (int) (offset/ConstantBroker.FILE_SIZE);
        fileQueue.get(queueOffset).putMessage(messageByte);

    }


    public CopyOnWriteArrayList getFileQueue() {
        return fileQueue;
    }

    public void setFileQueue(CopyOnWriteArrayList fileQueue) {
        this.fileQueue = fileQueue;
    }

    public long getFileWriteIndex() {
        return fileWriteIndex;
    }

    public void setFileWriteIndex(long fileWriteIndex) {
        this.fileWriteIndex = fileWriteIndex;
    }

    public int getQueueIndex() {
        return queueIndex;
    }

    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }
}
