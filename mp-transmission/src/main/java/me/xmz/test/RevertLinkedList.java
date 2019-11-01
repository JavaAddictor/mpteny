package me.xmz.test;

import me.xmz.timer.MimeTimer;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RevertLinkedList<V> {

    private Node head;

    private Node tail;

    private int size;

    final class Node<V> {
        private Node next;
        private V value;

        Node(V value) {
            this.value = value;
        }
    }

    void enque(V value) {
        Node node = new Node(value);
        if(head == null) {
            head = node;
        }else {
            tail.next = node;
        }
        tail = node;
    }

    public void add(V value) {
        enque(value);
        size++;
    }

    public Iterator<V> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<V> {

        int cursor;
        Node<V> currnode;
        @Override
        public boolean hasNext() {
            return cursor != size;
        }
        @Override
        public V next() {
            if(cursor >= size) {
                throw new NoSuchElementException();
            }
            if (cursor == 0) {
                currnode = head;
            }else {
                currnode = currnode.next;
            }
            cursor++;
            return currnode.value;
        }
    }

    public void revert() {
        if(size > 1) {
            Node hnext = head.next;
            Node n2 = hnext.next;
            head.next = null;
            hnext.next = head;
            Node pn = n2;
            while(pn != null) {
                pn = pn.next;
                n2.next = hnext;
                hnext = n2;
                n2 = pn;
            }
        }
        Node n = head;
        head = tail;
        tail = n;
    }

}
