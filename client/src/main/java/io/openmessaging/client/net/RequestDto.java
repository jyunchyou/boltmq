package io.openmessaging.client.net;

/**
 * Created by fbhw on 17-11-2.
 */
public class RequestDto {

    public static final long serialVersionUID = 1L;

    private String id = null;

    private String result = null;

    private String command = null;

    private String language = null;

    private String version = null;

    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }

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
        return "id:"+id+"command:"+command+"result:"+result;
    }
}
