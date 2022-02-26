package com.lois.tytool.basej.math;

/**
 * @Description 双向链路封装对象
 * @Author Luo.T.Y
 * @Date 2022/1/21 16:09
 */
public class Node {
    /**
     * 节点的值
     */
    private Object value;
    /**
     * 下一个节点
     */
    private Node next;
    /**
     * 上一个节点
     */
    private Node previous;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }
}
