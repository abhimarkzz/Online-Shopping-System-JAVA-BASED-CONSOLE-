# 🛒 Online Shopping System (JavaFX)

## 📌 Overview

This project is a **desktop-based Online Shopping System** built using **JavaFX and FXML**.
It simulates a real-world e-commerce application where users can shop, manage carts, place orders, and submit complaints.

The system also includes an **Admin panel** to manage users and complaints.

---

## 🚀 Features

### 👤 User Features

* User Registration & Login
* Browse Products
* Search & Sort Products
* Add to Cart
* Remove / Undo Cart Items
* Checkout & Place Orders
* View Order History
* Submit Complaints

### 🛠️ Admin Features

* View All Users
* View Orders
* Manage Complaints (OPEN → IN_REVIEW → RESOLVED)

---

## 🧠 Concepts Used

* JavaFX (UI)
* FXML (UI Design)
* MVC Architecture
* File Handling (Data Persistence)
* Custom Data Structures:

  * Linked List
  * Stack (Undo feature)
  * Queue
  * HashTable
* Sorting Algorithms
* Binary Search

---

## 📁 Project Structure

```
com.shopping
│
├── controllers     → Handles UI logic
├── models          → Data classes (User, Product, Order, etc.)
├── services        → Business logic & file handling
├── datastructures  → Custom implementations (Stack, Queue, etc.)
├── Main.java       → Entry point
```

---

## ▶️ How to Run

1. Open project in IntelliJ / Eclipse
2. Make sure JavaFX is configured
3. Run:

   ```
   Main.java
   ```
4. Application will start with Login screen

---

## 🧪 Default Login (if available)

```
Username: admin
Password: admin123
```

---

## ⚠️ Notes

* Data is stored using file handling (no database)
* Designed for learning DSA + JavaFX concepts
* Can be extended with database and payment integration

---

## 💡 Future Improvements

* Add database (MySQL / Firebase)
* Payment Gateway integration
* Better UI animations
* Product images support

---

## 👩‍💻 Author

Ruchee

---

## ⭐ Final Thoughts

This project demonstrates how **Data Structures + UI + File Handling** can be combined to build a real-world system.
