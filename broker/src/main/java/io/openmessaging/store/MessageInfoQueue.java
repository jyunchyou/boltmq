package io.openmessaging.store;

import io.openmessaging.Constant.ConstantBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbhw on 17-12-5.
 */


public class MessageInfoQueue {

    Logger logger = LoggerFactory.getLogger(MessageInfoQueue.class);

    private String queueId = null;

    private   List queue = new ArrayList<MessageInfo>();

    private int index;//consumer list index

    private long previousMessageIndex = 0;//上一个文件的index, 方便创建新文件时给fileIndex命名;

    private File file;

    public MessageInfoQueue(int name){

        this.file = new File(ConstantBroker.ROOT_PATH+name);
        if (!file.exists()){

                file.mkdir();
        }

    }



    public void setList(List list){
        this.queue = list;

    }

    public List getList(){
        return this.queue;
    }



    public long getPreviousMessageIndex() {
        return previousMessageIndex;
    }

    public void setPreviousMessageIndex(long previousMessageIndex) {
        this.previousMessageIndex = previousMessageIndex;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }
}
