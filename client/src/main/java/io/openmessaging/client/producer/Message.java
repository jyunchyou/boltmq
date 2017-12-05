package io.openmessaging.client.producer;

/**
 * Created by fbhw on 17-10-31.
 */
public class Message {

    private String topic = null;

    private String orderId = null;//顺序发送时定义组id

    private byte[] body = null;//消息内容

    public Message(String topic,String orderId, byte[] body){
        this.topic = topic;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    }
