package io.openmessaging.client.impl;

/**
 * Created by fbhw on 17-10-31.
 */
public class MessageImpl{

    private String topic = null;

    private String tag = null;

    private String orderId = null;

    private byte[] body = null;

    public MessageImpl(String topic, String tag, String orderId,byte[] body){
        this.topic = topic;
        this.tag = tag;
        this.orderId = orderId;
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
