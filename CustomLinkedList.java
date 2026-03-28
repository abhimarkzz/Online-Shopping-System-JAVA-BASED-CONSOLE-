package com.shopping.datastructures;

import java.io.Serializable;
import java.util.*;

public class CustomLinkedList<T> implements Serializable, Iterable<T> {
    private static final long serialVersionUID = 1L;

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomLinkedList() {}

    public void addLast(T data) {
        Node<T> n = new Node<>(data);
        if (tail == null) { head = tail = n; }
        else { tail.next = n; n.prev = tail; tail = n; }
        size++;
    }

    public void addFirst(T data) {
        Node<T> n = new Node<>(data);
        if (head == null) { head = tail = n; }
        else { n.next = head; head.prev = n; head = n; }
        size++;
    }

    public void add(T data) { addLast(data); }

    public T removeFirst() {
        if (head == null) return null;
        T data = head.data;
        head = head.next;
        if (head != null) head.prev = null; else tail = null;
        size--;
        return data;
    }

    public T removeLast() {
        if (tail == null) return null;
        T data = tail.data;
        tail = tail.prev;
        if (tail != null) tail.next = null; else head = null;
        size--;
        return data;
    }

    public boolean remove(T data) {
        Node<T> cur = head;
        while (cur != null) {
            if (cur.data.equals(data)) { unlink(cur); return true; }
            cur = cur.next;
        }
        return false;
    }

    public void set(int index, T data) {
        Node<T> n = nodeAt(index);
        if (n != null) n.data = data;
    }

    public T get(int index) {
        Node<T> n = nodeAt(index);
        return n != null ? n.data : null;
    }

    public T peekFirst() { return head != null ? head.data : null; }
    public T peekLast()  { return tail != null ? tail.data : null; }

    public int indexOf(T data) {
        int i = 0;
        for (T item : this) { if (item.equals(data)) return i; i++; }
        return -1;
    }

    public boolean contains(T data) {
        for (T item : this) if (item.equals(data)) return true;
        return false;
    }

    public int  size()      { return size; }
    public boolean isEmpty(){ return size == 0; }
    public void clear()     { head = tail = null; size = 0; }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (T item : this) list.add(item);
        return list;
    }

    public static <T> CustomLinkedList<T> fromList(List<T> list) {
        CustomLinkedList<T> ll = new CustomLinkedList<>();
        for (T item : list) ll.addLast(item);
        return ll;
    }

    private Node<T> nodeAt(int index) {
        if (index < 0 || index >= size) return null;
        Node<T> cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur;
    }

    private void unlink(Node<T> n) {
        if (n.prev != null) n.prev.next = n.next; else head = n.next;
        if (n.next != null) n.next.prev = n.prev; else tail = n.prev;
        size--;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> cur = head;
            public boolean hasNext() { return cur != null; }
            public T next() {
                if (cur == null) throw new NoSuchElementException();
                T d = cur.data; cur = cur.next; return d;
            }
        };
    }
}