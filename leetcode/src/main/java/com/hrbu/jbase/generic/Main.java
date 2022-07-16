package com.hrbu.jbase.generic;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Java泛型中的常见标记符含义：
 *
 * E - Element (在集合中使用，因为集合中存放的是元素)
 *
 * T - Type（Java 类）
 *
 * K - Key（键）
 *
 * V - Value（值）
 *
 * N - Number（数值类型）
 *
 * ？ - 通配符，表示不确定的java类型
 *
 */

public class Main extends ENode<Node> {

    @Test
    public void Main() {
        Node node = new Node("1", null);
        this.Test(node);
    }

}
class ENode<E extends Node> {

    List<E> list = new LinkedList();

    public void Test(E e) {

        list.add(e);

        System.out.println(e);
    }

    /*public void Test2() {
        Node node = new Node("1", null);

        Test(node);
    }*/


}


class Node {
    public String value;
    public Node next;

    public Node(String value, Node next) {
        this.value = value;
        this.next = next;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "Node{" +
                "value='" + value + '\'' +
                ", next=" + next +
                '}';
    }
}