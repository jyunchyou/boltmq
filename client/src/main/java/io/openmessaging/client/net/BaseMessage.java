package io.openmessaging.client.net;

import com.sun.org.apache.bcel.internal.classfile.Code;
import io.openmessaging.client.constant.ConstantClient;

import java.util.UUID;

/**
 * Created by fbhw on 17-11-2.
 */
public class BaseMessage {
    //消息头=============================================

    private final int constantLen = 44;

    private int totalSize;//消息总长 4字节

    private boolean isDelay;//是否延时

    private boolean isSeq;//是否顺序

    private boolean isCallBack;//是否返回

    private boolean isOneWay;//是否单向

    private boolean isSyn;//是否同步 判断位共一字节

    private long sendTimeStamp;//消息发送时的事件戳 8字节

    private long conserveTime;//消息保存时间,单位ms 8字节

    private byte delayTime;//延时时间,支持1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h，不等向短靠近 1字节

    private String delayTimeUnit;//延时消息单位,s,m,h 1字节

    //消息体=============================================
    private int batchNum = 1;

    private byte topicLength;//topic长度 1字节

    private String topic;

    private int valueLength;//value长度 4字节

    private byte[] value;

    //TODO,topic,value验证，文件加入魔数，载入验证魔数


    public BaseMessage(){

    }
    public BaseMessage(String topic,byte[] value){

        this.topic = topic;
        this.value = value;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public long getSendTimeStamp() {
        return sendTimeStamp;
    }

    public void setSendTimeStamp(long sendTimeStamp) {
        this.sendTimeStamp = sendTimeStamp;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public boolean isSyn() {
        return isSyn;
    }

    public void setSyn(boolean syn) {
        isSyn = syn;
    }

    public boolean isOneWay() {
        return isOneWay;
    }

    public void setOneWay(boolean oneWay) {
        isOneWay = oneWay;
    }

    public boolean isCallBack() {
        return isCallBack;
    }

    public void setCallBack(boolean callBack) {
        isCallBack = callBack;
    }

    public boolean isSeq() {
        return isSeq;
    }

    public void setSeq(boolean seq) {
        isSeq = seq;
    }

    public boolean isDelay() {
        return isDelay;
    }

    public void setDelay(boolean delay) {
        isDelay = delay;
    }

    public String getDelayTimeUnit() {
        return delayTimeUnit;
    }

    public void setDelayTimeUnit(String delayTimeUnit) {
        this.delayTimeUnit = delayTimeUnit;
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    public static void main(String[] args) {
        boolean b = true;
        byte bt = 0x1 + 1;
        int te = bt;


        System.out.println(te&1);

         }


    public int getValueLength() {
        return valueLength;
    }

    public void setValueLength(int valueLength) {
        this.valueLength = valueLength;
    }

    public int getConstantLen() {
        return constantLen;
    }

    public long getConserveTime() {
        return conserveTime;
    }

    public void setConserveTime(long conserveTime) {
        this.conserveTime = conserveTime;
    }

    public byte getTopicLength() {
        return topicLength;
    }

    public void setTopicLength(byte topicLength) {
        this.topicLength = topicLength;
    }

    public byte getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(byte delayTime) {
        this.delayTime = delayTime;
    }

}
