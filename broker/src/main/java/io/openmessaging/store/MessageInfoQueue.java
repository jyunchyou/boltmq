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

    public  List queue = new ArrayList<MessageInfo>();

    private String queueId;

    private long messageIndex;//文件命名为offset,可通过offset直接得到文件名,每个文件(MessageInfoQueue)最大为上一个文件index+1073741824(1G);

    private long previousMessageIndex = 0;//上一个文件的index, 方便创建新文件时给fileIndex命名;

    private File file;

    public MessageInfoQueue(int name){

        this.queueId = name + "";

        this.file = new File(ConstantBroker.ROOT_PATH+name);
        if (!file.exists()){

                file.mkdir();
        }

    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public void setList(List list){
        this.queue = list;

    }

    public List getList(){
        return this.queue;
    }

    public long getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(long fileIndex) {
        this.messageIndex = fileIndex;
    }

    public long getPreviousMessageIndex() {
        return previousMessageIndex;
    }

    public void setPreviousMessageIndex(long previousMessageIndex) {
        this.previousMessageIndex = previousMessageIndex;
    }
}
