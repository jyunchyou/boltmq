package io.openmessaging.client.selector;

import io.openmessaging.client.producer.MessageQueue;

import java.util.List;

/**
 * Created by fbhw on 17-10-31.
 */
public class QueueSelectByHash implements QueueSelector{


    public MessageQueue select(List<MessageQueue> messageQueues,Object arg){
        int hashCode = arg.hashCode();
        if (hashCode < 0) {
            hashCode = Math.abs(hashCode);

        }
        return messageQueues.get(hashCode % messageQueues.size());

    }
}
