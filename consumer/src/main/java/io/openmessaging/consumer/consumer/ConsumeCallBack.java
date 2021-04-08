package io.openmessaging.consumer.consumer;

import io.openmessaging.consumer.constant.ConstantConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ConsumeCallBack {

    private ArrayList<Message> messages = new ArrayList<Message>();

    public int pullNum;

    public Semaphore semaphore = null;

    public ConsumeCallBack(int pullNum){
        this.pullNum = pullNum;
        this.messages = new ArrayList<Message>(pullNum);
        semaphore = new Semaphore(ConstantConsumer.PULL_SEMAPhORE_NUM);

    }

    public synchronized void call(List<Message> messageList){

        for (Message message : messageList) {

            System.out.print(message.getTopic());
            System.out.println(new String(message.getBody()) + "|||");
        }

    }

    public synchronized  void call(Message message){

        if (pullNum > 0) {
            messages.add(message);
        }
        if (messages.size() == pullNum ) {
            call(messages);
            messages = new ArrayList<Message>(pullNum);
            semaphore.release();
        }

    }
}
