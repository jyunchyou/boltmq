package io.openmessaging.store;

/**
 * Created by fbhw on 17-12-5.
 */

//MessageInfo是Message的逻辑对象
public class MessageInfo {

    private String topic;

    private long offset;

    private long len;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }
}
