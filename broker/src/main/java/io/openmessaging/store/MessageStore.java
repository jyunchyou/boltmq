package io.openmessaging.store;

import io.netty.buffer.ByteBuf;

/**
 * Created by fbhw on 17-12-5.
 */
public class MessageStore {

    private static MessageStore messageStore = new MessageStore();

    private long offset = 0l;

    private MessageStore(){

    }

    public static MessageStore getMessageStore(){
        return messageStore;
    }



    public void input(ByteBuf byteBuf,MessageInfo fileInfo){






        }

    }




