package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class IndexFileQueue {

    private CopyOnWriteArrayList<AbstractIndexFile> indexFileQueue = new CopyOnWriteArrayList();


    private long index;

    private int queueIndex = 0;

    public IndexFileQueue(String topic){
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
}
