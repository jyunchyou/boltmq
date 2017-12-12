package io.openmessaging.table;


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

    private List list = new ArrayList<MessageInfo>();

    private String queueId;

    private long messageIndex;

    private long previousMessageIndex = 0;//上一个文件的index, 方便创建新文件时给fileIndex命名;

    private File file;



    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
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

    public NodeMessageInfo getNodeMessageInfo() {
        return nodeMessageInfo;
    }

    public void setNodeMessageInfo(NodeMessageInfo nodeMessageInfo) {
        this.nodeMessageInfo = nodeMessageInfo;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
