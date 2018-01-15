package io.openmessaging.table;

public class AbstractIndex {

    private long index = 0;

    private long len = 0;

    private long sendTime = 0;

    public AbstractIndex(long index,long len,long sendTime){
        this.index = index;
        this.len = len;
        this.sendTime = sendTime;

    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }
}
