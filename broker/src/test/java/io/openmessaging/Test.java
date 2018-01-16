package io.openmessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;





/*
霍夫曼编码是一种被广泛应用而且非常有效的数据压缩技术，它使用一张字符出现频度表，根据它来构造一种将每个字符表示成二进制串的最优方式，一般可压缩掉20%~90%。
        |---------+-----+-----+-----+-----+------+------|
        | letter  |   a |   b |   c |   d |    e |    f |
        |---------+-----+-----+-----+-----+------+------|
        | count   |  45 |  13 |  12 |  16 |    9 |    5 |
        | code    | 000 | 001 | 010 | 011 |  100 |  101 |
        | huffman |   0 | 101 | 100 | 111 | 1101 | 1100 |
        |---------+-----+-----+-----+-----+------+------|
        如上表所示，假设一篇文章包含45个字母a、13个字母b……，如果用长度相同的编码000、001等去存储这些信息，则需要（45＋13＋12＋16＋9＋5）×3＝300位空间。
        但如果换成可变长编码，即编码长度不固定的霍夫曼编码，则仅需要45×1＋13×3＋12×3＋16×3＋9×4＋5×4＝224位空间。
        现在给你这些原始数据，请你计算改用霍夫曼编码之后需要多少位空间。
        输入描述:

        输入包含多组数据，每组数据第一行包含一个正整数n（2≤n≤50），表示字符的数量。

        第二行包含n个正整数，分别表示这n个字符出现的次数，每个字符出现次数不超过10000。

        输出描述:

        对应每一组数据，输出使用霍夫曼编码需要多少位空间存储这些信息。

        示例1
        输入

        6
        45 13 12 16 9 5

        输出

        224

*/

public class Test {


    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);
        Test test = new Test();
        while(sc.hasNext()){
            int n =sc.nextInt();
            int[] A = new int[n];
            for(int i=0;i<n;i++)
                A[i] = sc.nextInt();
            test.compressHuffman(n,A);

        }

        }



    public  void compressHuffman(int num, int[] countNum) {
        List<Node> nodes = new ArrayList<Node>();

        Node bNode = null;

        for (int count : countNum) {
            Node dataNode = new Node(count);
            dataNode.setDataFlag(true);
            nodes.add(dataNode);

        }

        while (findFlag(nodes)) {
            Node smallNode = getSmallNode(nodes);
            Node smallSecondNode = getSmallNode(nodes);


            if (smallNode != null && smallSecondNode != null) {

                Node newNode = new Node(smallNode.getValue() + smallSecondNode.getValue());
                newNode.setLeftNode(smallNode);
                newNode.setRightNode(smallSecondNode);
                nodes.add(newNode);

                bNode = newNode;
            }
        }







    }

    public  int calculation(List<Node> nodes, Node bNode){


        int occupySize = 0;

        int deep = 0;

        for (Node node : nodes) {

            if (node.isDataFlag()) {






               deep = deep(0,bNode,node);
                occupySize += (deep * node.getValue());
            }
        }

        return occupySize;
    }



    public int deep(int deep,Node node,Node n){



        if (node == null || node.equals(n)) {


            return deep;
        }
        int d = deep+1;
        if (node.getLeftNode() != null) {

           int de =  deep(d,node.getLeftNode(),n);

           if (de != 0) {
               return de;
           }

        }
        if (node.getRightNode() != null) {
            int de = deep(d,node.getRightNode(),n);

            if (de != 0) {
                return de;
            }
        }





        return 0;
    }

    public boolean findFlag(List<Node> nodes){

        for (Node node : nodes) {
            if (node.isFlag()) {
                return true;
            }
        }
        return false;

    }

    public  Node getSmallNode(List<Node> nodes){

        Node smallNode = null;
        for (Node node : nodes) {
            if (node.isFlag()) {

                if (smallNode != null) {
                    if (smallNode.getValue() > node.getValue()) {

                        smallNode = node;
                    }

                }else {

                    smallNode = node;

                }


            }

        }
        if (smallNode == null) {
            return null;

        }
        smallNode.setFlag(false);

        return smallNode;

    }
}
class Node {
    private boolean flag = true;
    private int value;
    private Node leftNode;
    private Node rightNode;
    private boolean dataFlag = false;

    public Node(int value){
        this.value = value;

    }
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isDataFlag() {
        return dataFlag;
    }

    public void setDataFlag(boolean dataFlag) {
        this.dataFlag = dataFlag;
    }
    @Override
    public boolean equals(Object o){
        Node node = (Node) o;
        if (node.getValue() == getValue()) {
            return true;

        }
        return false;

    }
}