package io.openmessaging.client.selector;

import io.openmessaging.client.table.SendQueue;

import java.util.List;

/**
 * Created by fbhw on 17-10-31.
 */
public interface QueueSelector {

    SendQueue select(List<SendQueue> sendQueues, Object arg);

}
