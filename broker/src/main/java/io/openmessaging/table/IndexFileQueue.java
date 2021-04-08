package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class IndexFileQueue {

    private CopyOnWriteArrayList<AbstractIndexFile> indexFileQueue = new CopyOnWriteArrayList();

    private AbstractIndexFile abstractIndexFile;

    private long index;//文件大小*文件个数+文件内下标

    private int queueIndex = 0;//已用文件个数

    private long messageIndex;//最新消息的下标

    static {

    }

    public IndexFileQueue(String topic){
        //初始化索引root地址
        File root = new File(ConstantBroker.ROOT_INDEX_PATH.substring(0,ConstantBroker.ROOT_INDEX_PATH.lastIndexOf("\\")));
        if (!root.exists()) {
            root.mkdirs();
        }



        File file = new File(ConstantBroker.ROOT_INDEX_PATH+topic);
        if (!file.exists()) {
                file.mkdir();

        }

    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public CopyOnWriteArrayList<AbstractIndexFile> getIndexFileQueue() {
        return indexFileQueue;
    }

    public void setIndexFileQueue(CopyOnWriteArrayList<AbstractIndexFile> indexFileQueue) {
        this.indexFileQueue = indexFileQueue;
    }

    public int getQueueIndex() {
        return queueIndex;
    }

    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }

    public AbstractIndexFile getAbstractIndexFile() {
        return abstractIndexFile;
    }

    public void setAbstractIndexFile(AbstractIndexFile abstractIndexFile) {
        this.abstractIndexFile = abstractIndexFile;
    }

    public long getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(long messageIndex) {
        this.messageIndex = messageIndex;
    }
}
