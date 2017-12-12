package io.openmessaging;

import java.util.LinkedList;

/**
 * Created by fbhw on 17-12-12.
 */
public class Test {
    @org.junit.Test
    public void Test(){
    LinkedList linkedList = new LinkedList();

    linkedList.add("Message_01");
    linkedList.add("Message_02");
    System.out.println(linkedList.peek());
    System.out.println(linkedList.element());
    System.out.println(linkedList);



    }
}
