package io.openmessaging.table;

public class AbstractMessage {

    private byte[] messageByte = null;
    public AbstractMessage(byte[] messageByte){
        this.messageByte = messageByte;
    }

    public byte[] getMessageByte() {
        return messageByte;
    }

    public void setMessageByte(byte[] messageByte) {
        this.messageByte = messageByte;
    }
}
