package io.openmessaging.client.producer;

/**
 * Created by fbhw on 17-10-31.
 */
public class Message {

    private String topic = null;

    private String tag = null;

    private String orderId = null;

    private byte[] body = null;

    private int code = 0;

    public Message(String topic, String tag, String orderId, byte[] body, int code){
        this.topic = topic;
        this.tag = tag;
        this.orderId = orderId;
        this.body = body;
        this.code = code;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
