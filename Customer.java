package com.shopping.models;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private int    id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String address;
    private String phone;
    private boolean admin;
    private String registeredDate;

    public Customer() {}

    public Customer(int id, String username, String password, String email,
                    String fullName, String address, String phone, boolean admin) {
        this.id             = id;
        this.username       = username;
        this.password       = password;
        this.email          = email;
        this.fullName       = fullName != null ? fullName : "";
        // derive first/last from fullName
        String[] parts = this.fullName.split(" ", 2);
        this.firstName      = parts[0];
        this.lastName       = parts.length > 1 ? parts[1] : "";
        this.address        = address;
        this.phone          = phone;
        this.admin          = admin;
        this.registeredDate = java.time.LocalDate.now().toString();
    }

    public int    getId()             { return id; }
    public String getUsername()       { return username; }
    public String getPassword()       { return password; }
    public String getEmail()          { return email; }
    public String getFirstName()      { return firstName != null ? firstName : ""; }
    public String getLastName()       { return lastName  != null ? lastName  : ""; }
    public String getFullName()       { return fullName  != null ? fullName  : ""; }
    public String getAddress()        { return address; }
    public String getPhone()          { return phone; }
    public boolean isAdmin()          { return admin; }
    public String getRegisteredDate() { return registeredDate; }

    public void setId(int v)               { id = v; }
    public void setUsername(String v)      { username = v; }
    public void setPassword(String v)      { password = v; }
    public void setEmail(String v)         { email = v; }
    public void setFirstName(String v)     { firstName = v; fullName = v + " " + (lastName!=null?lastName:""); }
    public void setLastName(String v)      { lastName = v;  fullName = (firstName!=null?firstName:"") + " " + v; }
    public void setFullName(String v)      { fullName = v; String[] p=v.split(" ",2); firstName=p[0]; lastName=p.length>1?p[1]:""; }
    public void setAddress(String v)       { address = v; }
    public void setPhone(String v)         { phone = v; }
    public void setAdmin(boolean v)        { admin = v; }
    public void setRegisteredDate(String v){ registeredDate = v; }

    @Override public boolean equals(Object o) { return (o instanceof Customer) && id==((Customer)o).id; }
    @Override public int hashCode()           { return Integer.hashCode(id); }
    @Override public String toString()        { return "Customer{id="+id+", username='"+username+"'}"; }
}