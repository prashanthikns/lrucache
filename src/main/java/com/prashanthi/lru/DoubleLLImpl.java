package com.prashanthi.lru;

public class DoubleLLImpl<E>{

private Node head;
private Node tail;
private Integer size;

class Node{
    E data;
    Node prev;
    Node next;

    public Node(E data, Node prev, Node next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }
}

public DoubleLLImpl() {
    head = tail = null;
    size = 0;
}

boolean isEmpty(){
    if (head == null && tail == null)
        return true;
    return false;
}

public Integer getSize() {
    return size;
}

public Node insertAtEnd(E data) {
    // If empty Q, add new node
    if (isEmpty()){
        Node node = new Node(data, null, null);
        head = tail = node;
        size += 1;
        return node;
    }

    Node node = new Node(data, tail /*prev*/, null /*next*/);
    tail.next = node;
    tail = node;

    size += 1;
    return node;
}

public Node getHead() {
    return head;
}

public Node delete(Node delNode) {
    if (head == null && tail == null) {
        // empty list
        return null;
    }

    if (head == delNode && tail == delNode) {
        // one element list
        Node temp = head;
        head  = tail = null;
        size -= 1;
        return temp;
    }

    if (head == delNode) {
        // delete head in list > 1
        Node temp = head;
        head = head.next;
        head.prev = null;

        temp.next = temp.prev = null;
        size -= 1;
        return temp;
    }

    if (tail == delNode) {
        // delete tail in list > 1
        Node temp = tail;
        tail = tail.prev;
        tail.next = null;

        temp.next = temp.prev = null;
        size -= 1;
        return temp;
    }

    // delete middle node
    // check to make sure this is actually an existing node.
    if (delNode.prev != null && delNode.next != null) {
        delNode.prev.next = delNode.next;
        delNode.next.prev = delNode.prev;

        delNode.next = delNode.prev = null;
        size -= 1;
        return delNode;
    }
    return null;
}


public static void main(String[] args) {
    DoubleLLImpl<CacheEntry> list = new DoubleLLImpl<CacheEntry>();

    CacheEntry c1 = new CacheEntry(1, 100);
    CacheEntry c2 = new CacheEntry(2, 200);
    CacheEntry c3 = new CacheEntry(3, 300);
    CacheEntry c4 = new CacheEntry(4, 400);
    CacheEntry c5 = new CacheEntry(5, 500);

    DoubleLLImpl<CacheEntry>.Node n1 = list.insertAtEnd(c1);
    DoubleLLImpl<CacheEntry>.Node n2 = list.insertAtEnd(c2);
    System.out.println("Size should be 2, actual is " + list.getSize());

    list.delete(n1);
    list.delete(n1);
    System.out.println("Tried to delete already deleted node and it worked");

    list.delete(n2);
    System.out.println("Size should be 0, actual is " + list.getSize());

    n2 = list.insertAtEnd(c2);
    System.out.println("Size should be 1, actual is " + list.getSize());

    DoubleLLImpl<CacheEntry>.Node n3 = list.insertAtEnd(c3);
    DoubleLLImpl<CacheEntry>.Node n4 = list.insertAtEnd(c4);
    System.out.println("Size should be 3, actual is " + list.getSize());

    DoubleLLImpl<CacheEntry>.Node n5 = list.insertAtEnd(c5);
    System.out.println("Size should be 4, actual is " + list.getSize());
}

}
