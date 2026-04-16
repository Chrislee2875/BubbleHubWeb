# Bubble Hub - Milk Tea Shop Management System

**PRJ301_Assignment_BubbleHub**  
Group 2 [code_101] of class SE1914, semester SP26, FPTU HCM

---

## Project Overview

### System Name
**Bubble Hub** - An online milk tea shop management and ordering platform

### Purpose
- Provide a convenient and fast online ordering platform for milk tea lovers
- Optimize the management process for shop owners, including order processing, inventory tracking, and revenue management
- Enhance user experience and security by integrating modern technologies (QR Payments)

### Actors & Responsibilities

#### Guests (Visitors)
- Browse the product menu
- Search for products by name, category
- Register for a new account

#### Customers (Registered Users)
- Log in to personal account
- Manage shopping cart with customization options
- Place orders and make payments
- Track order status
- Update personal profile information

#### Admins (Store Managers)
- Manage product categories
- Handle customer orders and order statuses
- View sales statistics and reports
- Manage user accounts
- Manage promotions and discounts

---

## Technology Stack

### Database
- SQL Server 2019
- SQL Server Management Studio (SSMS) 21
- Account: sa-1234567890

### Java Web (MVC2 Architecture - No Scriptlets)
- JDK 8u371
- NetBeans 13
- Apache Tomcat 9.0.113
- SQLJDBC4
- JSTL 1.2 (javax)

---

## What Can Users Do?

### For Visitors (Anyone browsing the website)

**Exploring the Menu**
- [ ] Browse all available milk tea products and toppings without needing an account
- [ ] Search for specific drinks by name (e.g., "Brown Sugar Milk Tea")
- [ ] Filter products by category (Milk Tea, Fruit Tea, Toppings, Snacks)

**Getting Started**
- [ ] Create a new account using email and password

---

### For Registered Customers (Logged-in users)

**Account Access & Security**
- [ ] Sign in using registered email/username and password
- [ ] Stay logged in securely across sessions

**Profile Management**
- [ ] View personal profile information (name, phone)
- [ ] Update profile details (change name, phone number)
- [ ] Change password for security

**Shopping Experience**
- [ ] Browse and search all products just like visitors
- [ ] Add milk tea to shopping cart with customizations:
  - Choose sugar level: 0%, 25%, 50%, 75%, or 100%
  - Choose ice level: No ice, Less ice, or Normal ice
  - Select extra toppings: Pearls, Jelly, Pudding, Aloe Vera, etc.
- [ ] View shopping cart with all selected items and customizations
- [ ] Adjust quantity of items in cart (increase or decrease)
- [ ] Remove unwanted items from cart
- [ ] See real-time total price calculation including all customizations

**Ordering & Payment**
- [ ] Proceed to checkout with items in cart
- [ ] Choose payment method: VNPAY (Bank Transfer)
- [ ] Receive QR code for payment with order details
- [ ] Complete payment via mobile banking app
- [ ] Receive order confirmation after successful payment
- [ ] Track order status (Pending, Preparing, Completed)

---

### For Store Managers (Admin users)

**Dashboard & Statistics**
- [ ] View sales overview and key metrics
- [ ] See total revenue by day, week, or month
- [ ] Monitor popular products and bestsellers
- [ ] Track order completion rates

**Product Catalog Management**
- [ ] Add new products to the menu:
  - Enter product name, price
  - Upload product images
  - Assign product category
- [ ] View complete list of all products in the system
- [ ] Search for specific products by name or ID
- [ ] Update existing product information:
  - Change name, price, images
  - Mark products as available or unavailable
- [ ] Remove products from the menu (deactivate or delete)

**Customer Account Management**
- [ ] View list of all registered customers
- [ ] Search for customers by name or email address
- [ ] View customer details (registration date, order history, status)
- [ ] Activate or deactivate customer accounts
- [ ] Remove problematic accounts if necessary

**Order Management**
- [ ] View all customer orders in real-time
- [ ] See order details (products, customizations, total price, customer info)
- [ ] Update order status through workflow:
  - Mark as "Preparing" when making the drinks
  - Mark as "Completed" after successful serve
- [ ] Handle order cancellations or refunds if needed

**Payment Verification**
- [ ] Check payment status for orders
- [ ] Verify bank transfers against VNPAY codes
- [ ] Confirm successful payments

---

## System Features

### Security & Validation
The system automatically validates all user inputs to ensure data quality and prevent errors:
- Email addresses must be in correct format (example@domain.com)
- All required fields must be filled before submission
- Prices must be positive numbers
- Phone numbers must match Vietnamese format
- Passwords must meet security requirements

### User Interface
The platform provides a consistent and pleasant user experience:
- Clean, modern design that's easy to navigate
- Mobile-friendly responsive layout (works on phones, tablets, computers)
- Consistent header, footer, and navigation across all pages
- Fast loading times and smooth interactions
- Clear error messages and helpful guidance

---

## User Roles & Permissions

| Feature | Guest | Customer | Admin |
|---------|:-----:|:--------:|:-----:|
| View Products | Yes | Yes | Yes |
| Search Products | Yes | Yes | Yes |
| Register Account | Yes | No | No |
| Login | Yes | Yes | Yes |
| Add to Cart | No | Yes | Yes |
| Update Profile | No | Yes | Yes |
| Checkout & Payment | No | Yes | Yes |
| Manage Products (CRUD) | No | No | Yes |
| Manage Users | No | No | Yes |
| View All Orders | No | Own Orders | All Orders |
| View Statistics | No | No | Yes |

---

## Prerequisites

- SQL Server 2019 + SSMS 21
- JDK 8u371
- Apache Tomcat 9.0.113
- NetBeans 13
- SQLJDBC4 Driver
- JSTL 1.2 (javax)

---

## Team Members

**Group 2 [code_101] - SE1914 - Semester SP26**

| Student ID | Full Name | Role | Responsibilities |
|------------|-----------|------|------------------|
| SE193900 | Nguyễn Xuân Quang | Developer |  |
| SE196220 | Trần Thường Quang | Developer |  |
| SE191017 | Nguyễn Tấn Lộc | Developer |  |
| SE193083 | Nguyễn Lê Hữu Trí | Developer |  |

---

## Project Timeline

| Phase | Duration | Deliverables |
|-------|----------|-------------|
| Planning & Design | Week 1-2 | Requirements, Database design, Mockups |
| Development Sprint 1 | Week 3-4 | Authentication, User Management |
| Development Sprint 2 | Week 5-6 | Product Management, Shopping Cart |
| Development Sprint 3 | Week 7-8 | Payment Integration, Testing |
| Final Testing & Deployment | Week 9 | Bug fixes, Documentation, Presentation |

---

## GUI
**Menu page**<img width="1903" height="914" alt="ui_01" src="https://github.com/user-attachments/assets/896a498c-59ed-4b89-9cc9-1cfaafcb4398" />

**Login page**<img width="1903" height="914" alt="ui_02" src="https://github.com/user-attachments/assets/55f199bb-e3f7-46c5-9a52-de9b2a87cddc" />

**Register page**<img width="1903" height="914" alt="ui_03" src="https://github.com/user-attachments/assets/cac3d0a5-2d39-4d23-a7bb-52824b7ddf25" />

**Profile page**<img width="1903" height="914" alt="ui_04" src="https://github.com/user-attachments/assets/e4abd603-42e5-4ac6-80a1-b2cce4519783" />

**Order page**<img width="1903" height="914" alt="ui_05" src="https://github.com/user-attachments/assets/08d22bd0-7ca7-4562-aad3-1dd27f74b5a4" />

**Change-password page**<img width="1903" height="914" alt="ui_06" src="https://github.com/user-attachments/assets/8999d6a1-276b-4abe-b61c-6d1894b8c2a1" />

**Detail page**<img width="1903" height="914" alt="ui_07" src="https://github.com/user-attachments/assets/980c7375-7c10-4855-ad94-f5b8f42f8280" />

**Cart page**<img width="1903" height="914" alt="ui_08" src="https://github.com/user-attachments/assets/1c688383-2d97-4691-868c-92f6e2f52137" />

**Payment Demo**<img width="1903" height="914" alt="ui_09" src="https://github.com/user-attachments/assets/3416ddd5-cb72-4fcb-bb50-a6499cc7f149" />

**Report page**<img width="1903" height="914" alt="ui_10" src="https://github.com/user-attachments/assets/767b73be-b932-489a-a7bb-cbbd75cb984b" />

**Manage product page**
<img width="1903" height="914" alt="ui_11" src="https://github.com/user-attachments/assets/3dd114c6-4e7f-44a2-b11f-c4b48db5e31a" />

**Manage category page**
<img width="1903" height="914" alt="ui_11" src="https://github.com/user-attachments/assets/3dd114c6-4e7f-44a2-b11f-c4b48db5e31a" />

**Manage order page**
<img width="1903" height="914" alt="ui_14" src="https://github.com/user-attachments/assets/d5a7a129-d5c7-4fce-95c9-3be0f660e69a" />

**Product Form page**
<img width="1903" height="914" alt="ui_12" src="https://github.com/user-attachments/assets/d75479da-97b6-425b-8a78-7708841c036f" />

**Category Form page**
<img width="1903" height="914" alt="ui_13" src="https://github.com/user-attachments/assets/48a0fef8-b5e4-456c-8aa0-7536c8c22728" />

## Database

<img width="1302" height="794" alt="image" src="https://github.com/user-attachments/assets/93c70d42-b0f1-4c8f-bdb7-49d1feec5671" />


## **Appendices**

**System Flowchart**  
<img alt="flowchart" src="https://github.com/user-attachments/assets/886a3634-004b-4489-acdf-37867d450745" />

**Block Diagram**  
<img alt="blockdiagram" src="https://github.com/user-attachments/assets/181369ee-e99a-4846-b971-fd5db8b2d69c" />

---

 **Figma Design (Direct Link)**
- https://www.figma.com/file/wnnZmiMFKnOpR0mpz2dwpu/PRJ301_BubbleHubWeb

---
**Last Updated: 2026-03-26**
