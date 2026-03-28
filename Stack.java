package com.shopping.datastructures;

import java.util.ArrayList;
import java.util.List;

public class Stack<T> {

    private List<T> elements;

    public Stack() { this.elements = new ArrayList<>(); }

    public void push(T element) {
        try { elements.add(element); }
        catch (Exception e) { System.err.println("Stack push error: " + e.getMessage()); }
    }

    public T pop() {
        try {
            if (isEmpty()) throw new IllegalStateException("Stack is empty");
            return elements.remove(elements.size() - 1);
        } catch (Exception e) { System.err.println("Stack pop error: " + e.getMessage()); return null; }
    }

    public T peek() {
        try {
            if (isEmpty()) throw new IllegalStateException("Stack is empty");
            return elements.get(elements.size() - 1);
        } catch (Exception e) { System.err.println("Stack peek error: " + e.getMessage()); return null; }
    }

    public boolean isEmpty() { return elements.isEmpty(); }
    public int size() { return elements.size(); }
    public void clear() { elements.clear(); }

    @Override public String toString() { return elements.toString(); }
}