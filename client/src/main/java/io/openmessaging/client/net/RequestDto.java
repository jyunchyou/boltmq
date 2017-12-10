package io.openmessaging.client.net;

import com.sun.org.apache.bcel.internal.classfile.Code;
import io.openmessaging.client.constant.ConstantClient;

/**
 * Created by fbhw on 17-11-2.
 */
public class RequestDto {

    public static final long serialVersionUID = 1L;

    private String id = null;//序号

    private String language = ConstantClient.JAVA;//发送语言

    private String version = ConstantClient.VERSION;//序列化版本

    private String serialModel = ConstantClient.JSON;//序列化方式

    private int code;//注意是返回Broker结果　-1发送超时或失败;0未发送;1发送成功;

    private int delayTime = ConstantClient.DELAY_TIME;//规定允许的超时时间

    private String queueId = null; //broker上的索引地址

    //private transient CommandCustomHeader customHeader; 包含String topic

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString(){
        return "id:"+id+
                "language:"+language+"version:"+
                version+"serialModel:"+serialModel+
                "code:"+code+"delayTime:"+delayTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerialModel() {
        return serialModel;
    }

    public void setSerialModel(String serialModel) {
        this.serialModel = serialModel;
    }

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code = code;
    };

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }
}
