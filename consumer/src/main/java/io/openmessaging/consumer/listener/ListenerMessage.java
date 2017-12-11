package io.openmessaging.consumer.listener;

import io.openmessaging.consumer.consumer.Message;

import java.util.List;

/**
 * Created by fbhw on 17-12-7.
 */
public interface ListenerMessage {


    void listener(Message message);

    void listener(List<Message> list);//多条消费
}
