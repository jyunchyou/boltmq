package io.openmessaging.client.net;

/**
 * Created by fbhw on 17-11-2.
 */
public class RequestDto {

    public static final long serialVersionUID = 1L;

    private String id = null;

    private String command = null;

    private String language = null;

    private String version = null;

    private String serialModel = null;

    private int code;// ResponseCode

    //private transient CommandCustomHeader customHeader; 包含String topic

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String toString(){
        return "id:"+id+"command:"+command+"command:"+command+
                "language:"+language+"version:"+version+"serialModel:"+serialModel;
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
}
