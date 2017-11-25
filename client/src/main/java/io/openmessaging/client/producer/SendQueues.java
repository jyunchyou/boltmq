package io.openmessaging.client.producer;

import io.openmessaging.client.producer.SendQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbhw on 17-11-5.
 */
public class SendQueues {


    List messageQueues = new ArrayList<SendQueue>();



    public SendQueues() throws IOException {
    }


    public List getList(){
        if (messageQueues.size() == 0) {
            return null;
        }
        return this.messageQueues;

    }
}
