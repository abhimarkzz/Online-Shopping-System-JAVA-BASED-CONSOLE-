package com.shopping.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Product product;
    private int     quantity;

    public CartItem() {}
    public CartItem(Product product, int quantity) { this.product = product; this.quantity = quantity; }

    public Product getProduct()   { return product; }
    public int     getQuantity()  { return quantity; }
    public void setProduct(Product p)  { product = p; }
    public void setQuantity(int q)     { quantity = q; }

    public double getSubtotal()   { return product != null ? product.getPrice() * quantity : 0; }
    public double getTotalPrice() { return getSubtotal(); }

    @Override public boolean equals(Object o) {
        return (o instanceof CartItem) && product != null && product.equals(((CartItem)o).product);
    }
    @Override public int hashCode() { return product != null ? product.hashCode() : 0; }
}