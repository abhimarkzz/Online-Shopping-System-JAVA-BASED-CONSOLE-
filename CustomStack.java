package com.shopping.datastructures;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class CustomStack<T> implements Serializable, Iterable<T> {
    private static final long serialVersionUID = 1L;

    private final CustomLinkedList<T> list = new CustomLinkedList<>();

    public void push(T data)    { list.addFirst(data); }
    public T    pop()           { return list.removeFirst(); }
    public T    peek()          { return list.peekFirst(); }
    public boolean isEmpty()    { return list.isEmpty(); }
    public int     size()       { return list.size(); }
    public void    clear()      { list.clear(); }
    public List<T> toList()     { return list.toList(); }

    @Override public Iterator<T> iterator() { return list.iterator(); }
}