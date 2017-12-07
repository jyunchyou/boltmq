package io.openmessaging.client.selector;

import io.openmessaging.client.table.SendQueue;

import java.util.List;
import java.util.Random;

/**
 * Created by fbhw on 17-10-31.
 */
public class QueueSelectByRandom implements QueueSelector{

    public SendQueue select(List<SendQueue> sendQueues, Object arg){
        Random random = new Random(System.currentTimeMillis());
        int randomNum = random.nextInt();
        randomNum = Math.abs(randomNum);
        int indexNum = randomNum % sendQueues.size();
        return sendQueues.get(indexNum);



    }

    public SendQueue select(List<SendQueue> sendQueues){
       return  this.select(sendQueues,null);

    }


}
