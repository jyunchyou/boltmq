package io.openmessaging.client.table;

import io.openmessaging.client.producer.BrokerInfo;

import java.io.Serializable;

/**
 * Created by fbhw on 17-10-31.
 */
public class SendQueue implements Serializable{

    public static final long serialVersionUID = 1L;

    private String topicName = null;

    private String  queueId = null;

    private BrokerInfo brokerInfo = null;

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

    public BrokerInfo getBrokerInfo() {
        return brokerInfo;
    }

    public void setBrokerInfo(BrokerInfo brokerInfo) {
        this.brokerInfo = brokerInfo;
    }
}
