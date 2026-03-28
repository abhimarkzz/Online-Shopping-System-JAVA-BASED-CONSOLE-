package com.shopping.services;

import com.shopping.datastructures.HashTable;
import com.shopping.models.Customer;
import java.util.*;

public class CustomerService {

    private HashTable<String,Customer>  byUsername = new HashTable<>();
    private HashTable<Integer,Customer> byId       = new HashTable<>();
    private int nextId = 1;

    public void loadFromFile() {
        try {
            List<Customer> loaded = FileManager.loadCustomers();
            for (Customer c : loaded) {
                if (c.getId() >= nextId) nextId = c.getId()+1;
                byUsername.put(c.getUsername(), c);
                byId.put(c.getId(), c);
            }
            if (!byUsername.containsKey("admin")) createAdmin();
        } catch (Exception e) {
            System.err.println("[CustomerService] load: "+e.getMessage());
            createAdmin();
        }
    }

    private void createAdmin() {
        Customer a = new Customer(nextId++,"admin","admin123",
                "admin@shop.com","Admin User","HQ","0000000000",true);
        byUsername.put(a.getUsername(), a);
        byId.put(a.getId(), a);
        save();
    }

    /** Returns Customer on success, null if wrong credentials */
    public Customer login(String username, String password) {
        Customer c = byUsername.get(username);
        return (c != null && c.getPassword().equals(password)) ? c : null;
    }

    /** Returns new Customer on success, null if username taken */
    public Customer register(String username, String password,
                             String email, String fullName,
                             String address, String phone) {
        if (byUsername.containsKey(username)) return null;
        Customer c = new Customer(nextId++, username, password,
                email, fullName, address, phone, false);
        byUsername.put(c.getUsername(), c);
        byId.put(c.getId(), c);
        save(); return c;
    }

    public boolean emailExists(String email) {
        for (Customer c : byId.values())
            if (email != null && email.equalsIgnoreCase(c.getEmail())) return true;
        return false;
    }

    public Customer getById(int id)               { return byId.get(id); }
    public Customer getByUsername(String username){ return byUsername.get(username); }
    public List<Customer> getAllCustomers()        { return byId.values(); }

    public boolean updateCustomer(Customer updated) {
        if (!byId.containsKey(updated.getId())) return false;
        Customer old = byId.get(updated.getId());
        byUsername.remove(old.getUsername());
        byUsername.put(updated.getUsername(), updated);
        byId.put(updated.getId(), updated);
        save(); return true;
    }

    public boolean deleteCustomer(int id) {
        Customer c = byId.get(id);
        if (c == null) return false;
        byUsername.remove(c.getUsername());
        byId.remove(id);
        save(); return true;
    }

    public int size() { return byId.size(); }

    private void save() { FileManager.saveCustomers(byId.values()); }
}