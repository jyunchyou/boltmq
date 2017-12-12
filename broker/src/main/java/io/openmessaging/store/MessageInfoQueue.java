package io.openmessaging.store;


import io.openmessaging.common.NodeMessageInfo;
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

    private NodeMessageInfo nodeMessageInfo = null;

    private List list = new ArrayList<MessageInfo>();//TODO 索引删除 标记清除

    private String queueId;

    private int  index;//

    private long previousMessageIndex = 0;//上一个文件的index, 方便创建新文件时给fileIndex命名;

    private File file;

    private int size;

    private String topic;


    public MessageInfoQueue(String topic){
        this.topic = topic;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getPreviousMessageIndex() {
        return previousMessageIndex;
    }

    public void setPreviousMessageIndex(long previousMessageIndex) {
        this.previousMessageIndex = previousMessageIndex;
    }

    public NodeMessageInfo getNodeMessageInfo() {
        return nodeMessageInfo;
    }

    public void setNodeMessageInfo(NodeMessageInfo nodeMessageInfo) {
        this.nodeMessageInfo = nodeMessageInfo;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
