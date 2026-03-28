package com.shopping.datastructures;

import java.io.Serializable;

public class Node<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(T data) {
        this.data = data;
    }
}