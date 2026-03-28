package com.shopping.models;

import com.shopping.datastructures.CustomLinkedList;
import com.shopping.datastructures.CustomStack;

public class UserSession {

    private static UserSession instance;

    private Customer                   currentCustomer;
    private CustomLinkedList<CartItem> cart;
    private CustomStack<CartItem>      undoStack;
    private boolean                    loggedIn;

    private UserSession() {
        cart      = new CustomLinkedList<>();
        undoStack = new CustomStack<>();
    }

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void login(Customer c) {
        currentCustomer = c;
        loggedIn        = true;
        cart            = new CustomLinkedList<>();
        undoStack       = new CustomStack<>();
    }

    public void logout() {
        currentCustomer = null;
        loggedIn        = false;
        cart            = new CustomLinkedList<>();
        undoStack       = new CustomStack<>();
    }

    /** Primary accessor */
    public Customer getCurrentCustomer() { return currentCustomer; }
    /** Alias used by some controllers */
    public Customer getCurrentUser()     { return currentCustomer; }

    public CustomLinkedList<CartItem> getCart()    { return cart; }
    public CustomStack<CartItem>     getUndoStack() { return undoStack; }
    public boolean isLoggedIn()                     { return loggedIn; }

    public boolean isAdmin() {
        return currentCustomer != null && currentCustomer.isAdmin();
    }

    public void setCart(CustomLinkedList<CartItem> c) { cart = c; }

    public int getCartItemCount() {
        int count = 0;
        try { for (CartItem i : cart) count += i.getQuantity(); } catch (Exception e) { /* ignore */ }
        return count;
    }

    public double getCartTotal() {
        double total = 0;
        try { for (CartItem i : cart) total += i.getSubtotal(); } catch (Exception e) { /* ignore */ }
        return total;
    }

    /** Clears cart and undo stack */
    public void reset() {
        cart      = new CustomLinkedList<>();
        undoStack = new CustomStack<>();
    }
}