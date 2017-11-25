package io.openmessaging.client.producer;

import java.io.Serializable;

/**
 * Created by fbhw on 17-10-31.
 */
public class SendQueue implements Serializable{

    public static final long serialVersionUID = 1L;

    private String topicName = null;

    private String  queueId = null;

    private String brokerName = null;


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


    @Override
    public String toString(){
        return "serialVersionUID:"+serialVersionUID+"topicName:"+topicName+"queueId:"+queueId
                +"brokerName:"+brokerName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }
}
