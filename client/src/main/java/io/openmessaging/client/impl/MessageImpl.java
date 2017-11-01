package io.openmessaging.client.impl;

/**
 * Created by fbhw on 17-10-31.
 */
public class MessageImpl<H> {

    private String title = null;



    private H order = null;

    private PropertiesImpl implProperties = null;

    public MessageImpl(String title, H order, MessageImpl message){
        this.title = title;

        this.order = order;
        this.implProperties = implProperties;

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

    public PropertiesImpl getImplProperties(){
        return this.implProperties;
    }

    public void setImplProperties(PropertiesImpl implProperties){
        this.implProperties = implProperties;

    }




}
