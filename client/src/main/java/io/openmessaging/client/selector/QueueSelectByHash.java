package io.openmessaging.client.selector;

import io.openmessaging.client.producer.SendQueue;

import java.util.List;

/**
 * Created by fbhw on 17-10-31.
 */
public class QueueSelectByHash implements QueueSelector{


    public SendQueue select(List<SendQueue> sendQueues, Object arg){
        int hashCode = arg.hashCode();
        if (hashCode < 0) {
            hashCode = Math.abs(hashCode);

        }
        return sendQueues.get(hashCode % sendQueues.size());

    }


}
