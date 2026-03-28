package com.shopping.services;

import com.shopping.datastructures.*;
import com.shopping.models.Product;
import java.util.*;

public class ProductService {

    private CustomLinkedList<Product>                    products;
    private HashTable<Integer,Product>                   byId;
    private HashTable<String,CustomLinkedList<Product>>  byCategory;
    private int nextId = 1;

    public ProductService() {
        products   = new CustomLinkedList<>();
        byId       = new HashTable<>();
        byCategory = new HashTable<>();
    }

    public void loadFromCsv() {
        try {
            for (Product p : FileManager.loadProducts()) {
                if (p.getId() >= nextId) nextId = p.getId()+1;
                products.addLast(p);
                byId.put(p.getId(), p);
                addToCat(p);
            }
        } catch (Exception e) { System.err.println("[ProductService] load: "+e.getMessage()); }
    }

    public void saveToCsv() {
        try { FileManager.saveProducts(products.toList()); }
        catch (Exception e) { System.err.println("[ProductService] save: "+e.getMessage()); }
    }

    public void addProduct(Product p) {
        if (p.getId() == 0) p.setId(nextId++);
        else if (p.getId() >= nextId) nextId = p.getId()+1;
        products.addLast(p); byId.put(p.getId(),p); addToCat(p); saveToCsv();
    }

    public boolean removeProduct(int id) {
        Product p = byId.get(id);
        if (p == null) return false;
        products.remove(p); byId.remove(id); remFromCat(p); saveToCsv(); return true;
    }

    public boolean updateProduct(Product u) {
        Product ex = byId.get(u.getId());
        if (ex == null) return false;
        int idx = products.indexOf(ex);
        products.set(idx, u); byId.put(u.getId(),u);
        remFromCat(ex); addToCat(u); saveToCsv(); return true;
    }

    public Product      getById(int id)            { return byId.get(id); }
    public List<Product> getAllProducts()           { return products.toList(); }
    public List<String>  getAllCategories()         { return byCategory.keys(); }
    public List<String>  getCategories()           { return getAllCategories(); }

    public List<Product> getByCategory(String cat) {
        CustomLinkedList<Product> c = byCategory.get(cat);
        return c != null ? c.toList() : new ArrayList<>();
    }

    public List<Product> search(String q) {
        if (q == null || q.isBlank()) return getAllProducts();
        String lq = q.toLowerCase();
        List<Product> r = new ArrayList<>();
        for (Product p : products)
            if (p.getName().toLowerCase().contains(lq)
                    || p.getCategory().toLowerCase().contains(lq)
                    || p.getDescription().toLowerCase().contains(lq)) r.add(p);
        return r;
    }

    public List<Product> getSorted(Comparator<Product> cmp, SortingAlgorithms.Algorithm algo) {
        return SortingAlgorithms.sort(getAllProducts(), cmp, algo);
    }

    public List<Product> getProductsSortedByPrice()      { return getSorted(SortingAlgorithms.BY_PRICE_ASC,   SortingAlgorithms.Algorithm.HEAP); }
    public List<Product> getProductsSortedByPriceDesc()  { return getSorted(SortingAlgorithms.BY_PRICE_DESC,  SortingAlgorithms.Algorithm.HEAP); }
    public List<Product> getProductsSortedByName()       { return getSorted(SortingAlgorithms.BY_NAME_ASC,    SortingAlgorithms.Algorithm.MERGE); }
    public List<Product> getProductsSortedByRating()     { return getSorted(SortingAlgorithms.BY_RATING_DESC, SortingAlgorithms.Algorithm.HEAP); }
    public List<Product> getProductsSortedByPopularity() { return getSorted(SortingAlgorithms.BY_POPULARITY,  SortingAlgorithms.Algorithm.HEAP); }
    public List<Product> getProductsSortedByCategory()   { return getSorted(SortingAlgorithms.BY_CATEGORY,    SortingAlgorithms.Algorithm.MERGE); }

    public boolean decreaseStock(int id, int qty) {
        Product p = getById(id);
        if (p == null || p.getQuantity() < qty) return false;
        p.setQuantity(p.getQuantity()-qty); saveToCsv(); return true;
    }

    public int size() { return products.size(); }

    private void addToCat(Product p) {
        CustomLinkedList<Product> l = byCategory.get(p.getCategory());
        if (l == null) { l = new CustomLinkedList<>(); byCategory.put(p.getCategory(), l); }
        l.addLast(p);
    }
    private void remFromCat(Product p) {
        CustomLinkedList<Product> l = byCategory.get(p.getCategory());
        if (l != null) l.remove(p);
    }
}