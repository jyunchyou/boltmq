package io.openmessaging.client.common;

/**
 * Created by fbhw on 17-10-31.
 */
public class DefaultMessage<H> {

    private String title = null;



    private H order = null;

    private DefaultProperties defaultProperties = null;

    public DefaultMessage(String title,H order,DefaultMessage defaultMessage){
        this.title = title;

        this.order = order;
        this.defaultProperties = defaultProperties;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public H getOrder() {
        return order;
    }

    public void setOrder(H order) {
        this.order = order;
    }

    public DefaultProperties getDefaultProperties(){
        return this.defaultProperties;
    }

    public void setDefaultProperties(DefaultProperties defaultProperties){
        this.defaultProperties = defaultProperties;

    }




}
