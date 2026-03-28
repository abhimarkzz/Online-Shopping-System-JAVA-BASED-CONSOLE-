package com.shopping.models;

import java.io.Serializable;

public class Product implements Serializable, Comparable<Product> {
    private static final long serialVersionUID = 1L;

    private int    id;
    private String name;
    private double price;
    private String category;
    private String description;
    private int    quantity;
    private double rating;
    private int    reviewCount;
    private String imageUrl;

    public Product() {}

    public Product(int id, String name, double price, String category,
                   String description, int quantity, double rating, String imageUrl) {
        this.id          = id;
        this.name        = name;
        this.price       = price;
        this.category    = category;
        this.description = description;
        this.quantity    = quantity;
        this.rating      = rating;
        this.reviewCount = 100;
        this.imageUrl    = imageUrl != null ? imageUrl : "";
    }

    public int    getId()           { return id; }
    public String getName()         { return name; }
    public double getPrice()        { return price; }
    public String getCategory()     { return category; }
    public String getDescription()  { return description; }
    public int    getQuantity()     { return quantity; }
    public int    getStock()        { return quantity; }      // alias
    public double getRating()       { return rating; }
    public int    getReviewCount()  { return reviewCount; }
    public String getImageUrl()     { return imageUrl; }

    public void setId(int v)           { id = v; }
    public void setName(String v)      { name = v; }
    public void setPrice(double v)     { price = v; }
    public void setCategory(String v)  { category = v; }
    public void setDescription(String v){ description = v; }
    public void setQuantity(int v)     { quantity = v; }
    public void setRating(double v)    { rating = v; }
    public void setReviewCount(int v)  { reviewCount = v; }
    public void setImageUrl(String v)  { imageUrl = v; }

    @Override public int compareTo(Product o) { return Double.compare(price, o.price); }
    @Override public boolean equals(Object o) { return (o instanceof Product) && id == ((Product)o).id; }
    @Override public int hashCode()           { return Integer.hashCode(id); }

    public String toCsv() {
        return id+","+q(name)+","+price+","+q(category)+","+q(description)
                +","+quantity+","+rating+","+reviewCount+","+q(imageUrl);
    }
    private String q(String s) {
        if (s==null) return "";
        if (s.contains(",") || s.contains("\"")) return "\""+s.replace("\"","\"\"")+"\"";
        return s;
    }

    public static Product fromCsv(String line) {
        try {
            String[] p = parseLine(line);
            if (p.length < 7) return null;
            Product pr = new Product(
                    Integer.parseInt(p[0].trim()), p[1].trim(),
                    Double.parseDouble(p[2].trim()), p[3].trim(), p[4].trim(),
                    Integer.parseInt(p[5].trim()), Double.parseDouble(p[6].trim()),
                    p.length > 8 ? p[8].trim() : "");
            if (p.length > 7) { try { pr.setReviewCount(Integer.parseInt(p[7].trim())); } catch(Exception ignored){} }
            return pr;
        } catch (Exception e) { return null; }
    }

    private static String[] parseLine(String line) {
        java.util.List<String> t = new java.util.ArrayList<>();
        StringBuilder sb = new StringBuilder(); boolean inQ = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c=='"') { if (inQ && i+1<line.length() && line.charAt(i+1)=='"') { sb.append('"'); i++; } else inQ=!inQ; }
            else if (c==',' && !inQ) { t.add(sb.toString()); sb.setLength(0); }
            else sb.append(c);
        }
        t.add(sb.toString());
        return t.toArray(new String[0]);
    }
}