package io.openmessaging.consumer.listener;

import io.openmessaging.consumer.consumer.Message;

/**
 * Created by fbhw on 17-12-7.
 */
public interface ListenerMessage {


    void listener(Message message);
}
