package io.openmessaging.client.handler;

public class Node {
    int value;
    Node next;
    public Node(int value){
        this.value = value;
    }
    public static  Node head = null;
    public static void main(String[] args) {
        Node node1 = new Node(8);
        Node node2 = new Node(9);
        Node node3 = new Node(9);
        node2.next = node3;
        node3.next =null;
        node1.next = node2;
        Node get = getFirst(node1,2);
        System.out.println(1);
    }
    public static Node getFirst(Node node,int n){
        // 9 -> 9 -> 9    +    2   =1001
        //n = 2   node  = 9

//        loop(node,n,false);

        Node.head = node;
        loop(node,node,n);


        return Node.head;
    }
    public static int helper(Node node,int n){

        if(node.next==null){
            int tmp = node.value + n;
            node.value = tmp%10;
            int ad = tmp/10;

            return ad;
        }else {

            int tmp = helper(node.next,n) + node.value;
            node.value = tmp%10;
            int ad = tmp/10;
            return ad;

        }

    }
    public static int loop(Node head,Node node,int n){

        if (node.next == null) {
            int sum = n+node.value;
            node.value = (sum)%10;
            return (sum)/10;
        }

        int res = loop(head,node.next,n);

        int sum = res + node.value;

        node.value = sum%10;
        if (sum >= 10 && node == head) {
            Node newHead = new Node(sum/10);
            newHead.next = head;

            Node.head = newHead;
        }


        return sum/10;
    }
}
