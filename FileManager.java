package com.shopping.services;

import com.shopping.models.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {

    private static final String DATA_DIR       = "data/";
    private static final String PRODUCTS_CSV   = DATA_DIR + "products.csv";
    private static final String ORDERS_CSV     = DATA_DIR + "orders.csv";

    /** Called by OnlineShoppingApp on startup */
    public static void initializeDataFiles() {
        ensureDir();
        if (!new File(PRODUCTS_CSV).exists()) writeDefaultProducts();
    }

    public static void ensureDir() {
        try { Files.createDirectories(Paths.get(DATA_DIR)); }
        catch (Exception e) { System.err.println("[FileManager] ensureDir: " + e.getMessage()); }
    }

    // ── Products ──────────────────────────────────────────────────────────

    public static List<Product> loadProducts() {
        ensureDir();
        List<Product> list = new ArrayList<>();
        try {
            File f = new File(PRODUCTS_CSV);
            if (!f.exists()) writeDefaultProducts();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (!line.trim().isEmpty()) {
                    Product p = Product.fromCsv(line.trim());
                    if (p != null) list.add(p);
                }
            }
            br.close();
        } catch (Exception e) { System.err.println("[FileManager] loadProducts: " + e.getMessage()); }
        return list;
    }

    public static void saveProducts(List<Product> products) {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_CSV))) {
            pw.println("id,name,price,category,description,quantity,rating,reviewCount,imageUrl");
            for (Product p : products) pw.println(p.toCsv());
        } catch (Exception e) { System.err.println("[FileManager] saveProducts: " + e.getMessage()); }
    }

    private static void writeDefaultProducts() {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_CSV))) {
            pw.println("id,name,price,category,description,quantity,rating,reviewCount,imageUrl");
            pw.println("1,iPhone 15 Pro,1199.99,Electronics,Latest Apple iPhone with titanium design,50,4.8,1240,");
            pw.println("2,Samsung Galaxy S24 Ultra,1099.99,Electronics,Samsung flagship with AI features,40,4.7,980,");
            pw.println("3,Sony WH-1000XM5,349.99,Electronics,Industry leading noise cancellation headphones,80,4.9,2100,");
            pw.println("4,MacBook Air M3,1299.99,Laptops,Apple Silicon powered ultrabook,30,4.9,870,");
            pw.println("5,Dell XPS 15,1499.99,Laptops,Premium Windows laptop with OLED display,25,4.6,540,");
            pw.println("6,Logitech MX Master 3S,99.99,Accessories,Professional wireless mouse,100,4.8,3200,");
            pw.println("7,Nike Air Max 270,129.99,Footwear,Iconic Nike sneakers with Air unit,60,4.5,4500,");
            pw.println("8,Adidas Ultraboost 23,179.99,Footwear,Premium running shoes with Boost cushioning,45,4.6,3800,");
            pw.println("9,The Pragmatic Programmer,49.99,Books,Classic software development book,200,4.9,6200,");
            pw.println("10,Clean Code,44.99,Books,Guide to writing clean maintainable code,150,4.7,5400,");
            pw.println("11,IKEA ALEX Desk,249.99,Furniture,Compact office desk with drawers,20,4.3,780,");
            pw.println("12,Herman Miller Aeron,1395.00,Furniture,Ergonomic office chair,10,4.9,920,");
            pw.println("13,Nespresso Vertuo,149.99,Kitchen,Premium pod coffee machine,35,4.6,1670,");
            pw.println("14,Instant Pot Duo,89.99,Kitchen,7-in-1 multi-use pressure cooker,55,4.7,8900,");
            pw.println("15,GoPro Hero 12,399.99,Electronics,Waterproof action camera 5.3K video,30,4.5,1100,");
            pw.println("16,Kindle Paperwhite,139.99,Electronics,6.8 inch e-reader with warm light,70,4.6,4200,");
            pw.println("17,Levis 501 Jeans,69.99,Clothing,Original straight fit jeans,120,4.4,7800,");
            pw.println("18,Uniqlo Down Jacket,79.99,Clothing,Packable lightweight puffer jacket,90,4.5,3300,");
            pw.println("19,Weber Spirit II Gas Grill,499.99,Outdoor,3-burner gas grill,15,4.7,650,");
            pw.println("20,Dyson V15 Detect,649.99,Appliances,Cordless vacuum with laser dust detection,20,4.8,1560,");
        } catch (Exception e) { System.err.println("[FileManager] writeDefaults: " + e.getMessage()); }
    }

    // ── Orders ────────────────────────────────────────────────────────────

    public static void saveOrder(Order o) {
        ensureDir();
        try {
            File f = new File(ORDERS_CSV);
            boolean newFile = !f.exists();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
                if (newFile) pw.println("orderId,customerId,customerName,customerEmail,shippingAddress,items,totalAmount,status,orderDate");
                pw.println(toCsvRow(o));
            }
        } catch (Exception e) { System.err.println("[FileManager] saveOrder: " + e.getMessage()); }
    }

    public static void saveAllOrders(List<Order> orders) {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ORDERS_CSV))) {
            pw.println("orderId,customerId,customerName,customerEmail,shippingAddress,items,totalAmount,status,orderDate");
            for (Order o : orders) pw.println(toCsvRow(o));
        } catch (Exception e) { System.err.println("[FileManager] saveAllOrders: " + e.getMessage()); }
    }

    public static List<String[]> loadOrdersRaw() {
        ensureDir();
        List<String[]> rows = new ArrayList<>();
        try {
            File f = new File(ORDERS_CSV);
            if (!f.exists()) return rows;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (!line.trim().isEmpty()) rows.add(line.split(",", 9));
            }
            br.close();
        } catch (Exception e) { System.err.println("[FileManager] loadOrdersRaw: " + e.getMessage()); }
        return rows;
    }

    private static String toCsvRow(Order o) {
        return q(o.getOrderId())+","+o.getCustomerId()+","+q(o.getCustomerName())+","
                +q(o.getCustomerEmail())+","+q(o.getShippingAddress())+","
                +q(o.getItemsCsvString())+","+String.format("%.2f",o.getTotalAmount())+","
                +o.getStatus().name()+","+q(o.getOrderDate());
    }

    private static String q(String s) {
        if (s==null) return "";
        if (s.contains(",") || s.contains("\"")) return "\""+s.replace("\"","\"\"")+"\"";
        return s;
    }

    // ── Serialization ─────────────────────────────────────────────────────

    public static void serialize(Object obj, String filename) {
        ensureDir();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR+filename))) {
            oos.writeObject(obj);
        } catch (Exception e) { System.err.println("[FileManager] serialize: " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_DIR+filename))) {
            return (T) ois.readObject();
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) { System.err.println("[FileManager] deserialize: " + e.getMessage()); }
        return null;
    }

    public static void saveCustomers(List<Customer> list)   { serialize(list, "customers.ser"); }
    public static void saveComplaints(List<Complaint> list) { serialize(list, "complaints.ser"); }

    public static List<Customer> loadCustomers() {
        List<Customer> c = deserialize("customers.ser");
        return c != null ? c : new ArrayList<>();
    }

    public static List<Complaint> loadComplaints() {
        List<Complaint> c = deserialize("complaints.ser");
        return c != null ? c : new ArrayList<>();
    }
}