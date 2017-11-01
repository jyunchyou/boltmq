package io.openmessaging.client.selector;

import io.openmessaging.client.impl.MessageQueue;

import java.util.List;

/**
 * Created by fbhw on 17-10-31.
 */
public interface QueueSelector {

    MessageQueue select(List<MessageQueue> messageQueues, Object arg);

}
