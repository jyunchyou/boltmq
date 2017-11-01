package io.openmessaging.client.impl;

import java.io.Serializable;

/**
 * Created by fbhw on 17-10-31.
 */
public class MessageQueue implements Serializable{

    public static final long serialVersionUID = 1L;

    private String topicName = null;

    private String  queueId = null;

    private String brokerLocal = null;


    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public String getBrokerLocal() {
        return brokerLocal;
    }

    public void setBrokerLocal(String brokerLocal) {
        this.brokerLocal = brokerLocal;
    }
}
