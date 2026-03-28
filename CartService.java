package com.shopping.services;

import com.shopping.datastructures.CustomLinkedList;
import com.shopping.datastructures.CustomStack;
import com.shopping.models.CartItem;
import com.shopping.models.Product;
import com.shopping.models.UserSession;

import java.util.List;

/**
 * Manages the shopping cart.
 * Cart items stored in CustomLinkedList (ordered).
 * Undo last-add stored in CustomStack.
 */
public class CartService {

    private final UserSession session;

    public CartService() {
        this.session = UserSession.getInstance();
    }

    // ── Add / Remove ──────────────────────────────────────────────────────────

    public boolean addToCart(Product product, int quantity) {
        try {
            if (product == null || quantity <= 0) return false;
            if (product.getQuantity() < quantity) {
                System.out.println("[CartService] Insufficient stock for: " + product.getName());
                return false;
            }
            CustomLinkedList<CartItem> cart = session.getCart();
            // Check if already in cart
            for (CartItem item : cart) {
                if (item.getProduct().getId() == product.getId()) {
                    int newQty = item.getQuantity() + quantity;
                    if (newQty > product.getQuantity()) {
                        System.out.println("[CartService] Not enough stock.");
                        return false;
                    }
                    // Push undo snapshot
                    session.getUndoStack().push(new CartItem(product, item.getQuantity()));
                    item.setQuantity(newQty);
                    return true;
                }
            }
            CartItem newItem = new CartItem(product, quantity);
            cart.addLast(newItem);
            session.getUndoStack().push(new CartItem(product, 0)); // 0 = was not in cart
            return true;
        } catch (Exception e) {
            System.err.println("[CartService] addToCart error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFromCart(Product product) {
        try {
            CustomLinkedList<CartItem> cart = session.getCart();
            for (CartItem item : cart) {
                if (item.getProduct().getId() == product.getId()) {
                    cart.remove(item);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("[CartService] removeFromCart error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateQuantity(Product product, int newQty) {
        try {
            if (newQty <= 0) return removeFromCart(product);
            if (newQty > product.getQuantity()) return false;
            for (CartItem item : session.getCart()) {
                if (item.getProduct().getId() == product.getId()) {
                    item.setQuantity(newQty);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("[CartService] updateQuantity error: " + e.getMessage());
        }
        return false;
    }

    /** Undo last add-to-cart operation */
    public boolean undoLastAdd() {
        try {
            CustomStack<CartItem> undo = session.getUndoStack();
            if (undo.isEmpty()) return false;
            CartItem snapshot = undo.pop();
            CustomLinkedList<CartItem> cart = session.getCart();
            for (CartItem item : cart) {
                if (item.getProduct().getId() == snapshot.getProduct().getId()) {
                    if (snapshot.getQuantity() == 0) {
                        cart.remove(item); // was not in cart before, remove entirely
                    } else {
                        item.setQuantity(snapshot.getQuantity()); // restore previous qty
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("[CartService] undoLastAdd error: " + e.getMessage());
        }
        return false;
    }

    public void clearCart() {
        try {
            session.getCart().clear();
            session.getUndoStack().clear();
        } catch (Exception e) {
            System.err.println("[CartService] clearCart error: " + e.getMessage());
        }
    }

    // ── Totals ────────────────────────────────────────────────────────────────

    public double getTotal() {
        try {
            double total = 0;
            for (CartItem item : session.getCart()) total += item.getSubtotal();
            return total;
        } catch (Exception e) {
            System.err.println("[CartService] getTotal error: " + e.getMessage());
            return 0.0;
        }
    }

    public int getItemCount() {
        try {
            int count = 0;
            for (CartItem item : session.getCart()) count += item.getQuantity();
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<CartItem> getCartItems() {
        try {
            return session.getCart().toList();
        } catch (Exception e) {
            System.err.println("[CartService] getCartItems error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public boolean isEmpty() { return session.getCart().isEmpty(); }
}