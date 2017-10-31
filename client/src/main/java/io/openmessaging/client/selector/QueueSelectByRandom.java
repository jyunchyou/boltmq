package io.openmessaging.client.selector;

import io.openmessaging.client.producer.MessageQueue;

import java.util.List;
import java.util.Random;

/**
 * Created by fbhw on 17-10-31.
 */
public class QueueSelectByRandom implements QueueSelector{

    public MessageQueue select(List<MessageQueue> messageQueues){
        Random random = new Random(System.currentTimeMillis());
        int randomNum = random.nextInt();
        int indexNum = randomNum % messageQueues.size();
        return messageQueues.get(indexNum);

    }



}
