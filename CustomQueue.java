package com.shopping.datastructures;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class CustomQueue<T> implements Serializable, Iterable<T> {
    private static final long serialVersionUID = 1L;

    private final CustomLinkedList<T> list = new CustomLinkedList<>();

    public void enqueue(T data) { list.addLast(data); }
    public T    dequeue()       { return list.removeFirst(); }
    public T    peek()          { return list.peekFirst(); }
    public boolean isEmpty()    { return list.isEmpty(); }
    public int     size()       { return list.size(); }
    public void    clear()      { list.clear(); }
    public List<T> toList()     { return list.toList(); }

    @Override public Iterator<T> iterator() { return list.iterator(); }
}