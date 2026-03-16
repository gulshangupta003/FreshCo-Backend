# 🍃 FreshCo Backend

[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](LICENSE)

**FreshCo** is a full-stack e-commerce platform connecting local vegetable and fruit shopkeepers with nearby customers. This repository contains the **backend REST API** built with Spring Boot, featuring JWT authentication, role-based access control, cart management, order processing, and comprehensive error handling.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Database Design](#-database-design)
- [API Reference](#-api-reference)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [Project Structure](#-project-structure)
- [Key Design Decisions](#-key-design-decisions)
- [Error Handling](#-error-handling)

---

## ✨ Features

### Authentication & Authorization
- JWT-based stateless authentication
- Role-based access control (ADMIN, SELLER, CUSTOMER)
- Custom `@PreAuthorize` method-level security
- Centralized error handling with `HandlerExceptionResolver` delegation pattern

### Seller Features
- Create and manage a single shop per seller
- Add, update, delete, and toggle product availability
- View and manage incoming orders with status workflow
- Dashboard with order count statistics by status

### Customer Features
- Browse shops and products with pagination
- Search products by name (case-insensitive, partial match)
- Filter products by category
- Persistent shopping cart with single-shop enforcement
- Multiple delivery addresses (max 5) with default address management
- Place orders with Cash on Delivery
- Cancel pending orders with automatic stock restoration

### Order Management
- Complete order lifecycle: `PENDING → CONFIRMED → PROCESSING → OUT_FOR_DELIVERY → DELIVERED`
- Validated status transitions (no skipping steps, no going backward)
- Cancellation allowed from PENDING (customer) or PENDING/CONFIRMED (seller)
- Automatic stock reduction on order placement
- Automatic stock restoration on cancellation
- Price locking at time of order (immune to future price changes)

---

## 🛠 Tech Stack

| Technology | Purpose |
|---|---|
| **Java 21** | Programming language |
| **Spring Boot 4.0.2** | Application framework |
| **Spring Security** | Authentication & authorization |
| **Spring Data JPA** | Database access & ORM |
| **Hibernate 7** | JPA implementation |
| **MySQL 8** | Relational database |
| **JWT (jjwt 0.13)** | Token-based authentication |
| **Lombok** | Boilerplate reduction |
| **Jakarta Validation** | Input validation |
| **Maven** | Build & dependency management |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Client (Postman / Frontend)       │
└──────────────────────────┬──────────────────────────┘
                           │ HTTP Request
                           ▼
┌─────────────────────────────────────────────────────┐
│                 JwtAuthenticationFilter              │
│            (extracts & validates JWT token)          │
└──────────────────────────┬──────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────┐
│                    Controller Layer                  │
│         (request mapping, validation, auth)         │
└──────────────────────────┬──────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────┐
│                    Service Layer                     │
│          (business logic, ownership checks)          │
└──────────────────────────┬──────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────┐
│                   Repository Layer                   │
│            (Spring Data JPA, database access)        │
└──────────────────────────┬──────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────┐
│                      MySQL Database                  │
│                  (9 tables, InnoDB)                  │
└─────────────────────────────────────────────────────┘
```

### Error Handling Architecture

All exceptions — including security-level exceptions — are handled in a single `GlobalExceptionHandler` using the **HandlerExceptionResolver delegation pattern**:

```
Security Filters                         GlobalExceptionHandler
┌──────────────────────────┐            ┌──────────────────────────┐
│ AuthenticationEntryPoint │─── 401 ──▶│ AuthenticationException  │
│ AccessDeniedHandler      │─── 403 ──▶│ AccessDeniedException    │
└──────────────────────────┘            │ MethodArgumentNotValid   │
                                        │ BadRequestException      │
Controllers ─── errors ───────────────▶│ ResourceNotFoundException│
                                        │ DuplicateResourceException│
                                        │ Exception (catch-all)    │
                                        └──────────────────────────┘
```

All error responses follow the **RFC 7807 ProblemDetail** standard.

---

## 🗃 Database Design

### ER Diagram

```
users ──── 1:1 ──── shops ──── 1:M ──── products ──── M:1 ──── categories
  │                   │                     │
  │ 1:1               │ 1:M                │ M:1
  │                   │                     │
 carts ── 1:M ── cart_items               │
  │                                        │
  │ 1:M                                    │
  │                                        │
orders ── 1:M ── order_items ─── M:1 ─────┘
  │
  │ M:1
  │
addresses
```

### Tables (9)

| Table | Description | Key Relationships |
|---|---|---|
| `users` | All users (customers, sellers) | Has one shop, one cart, many addresses, many orders |
| `shops` | Seller storefronts | Belongs to one user, has many products and orders |
| `categories` | Product categories | Has many products |
| `products` | Items sold by shops | Belongs to one shop and one category |
| `addresses` | Delivery addresses | Belongs to one user |
| `carts` | Shopping carts | Belongs to one user, references one shop |
| `cart_items` | Products in cart | Belongs to one cart, references one product |
| `orders` | Customer orders | Belongs to one customer, shop, and address |
| `order_items` | Products in order | Belongs to one order, references one product |

---

## 📡 API Reference

### Authentication (Public)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register new user (SELLER or CUSTOMER) |
| `POST` | `/api/auth/login` | Login and receive JWT token |
| `GET` | `/api/roles` | Get all available roles |

### User

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/users/me` | Any authenticated | Get logged-in user's profile |

### Shop

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/shops` | SELLER | Create shop |
| `GET` | `/api/shops/me` | SELLER | Get my shop |
| `GET` | `/api/shops/me/orders/count` | SELLER | Dashboard order counts |
| `GET` | `/api/shops` | Any authenticated | List all shops |
| `GET` | `/api/shops/{id}` | Any authenticated | Get shop by ID |
| `PUT` | `/api/shops/{id}` | Shop owner | Update shop |
| `DELETE` | `/api/shops/{id}` | Shop owner | Delete shop |
| `GET` | `/api/shops/{shopId}/products` | Any authenticated | Shop's products (paginated) |
| `GET` | `/api/shops/{shopId}/orders` | Shop owner | Shop's orders (paginated) |

### Category (Public)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/categories` | Create category |
| `GET` | `/api/categories` | List all categories |
| `GET` | `/api/categories/{id}` | Get category by ID |
| `PUT` | `/api/categories/{id}` | Update category |
| `DELETE` | `/api/categories/{id}` | Delete category (idempotent) |
| `GET` | `/api/categories/{id}/products` | Category's products (paginated) |

### Product

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/products` | SELLER | Create product |
| `GET` | `/api/products/search?q=tom` | Any authenticated | Search by name (paginated) |
| `GET` | `/api/products` | Any authenticated | List all products (paginated) |
| `GET` | `/api/products/{id}` | Any authenticated | Get product by ID |
| `PUT` | `/api/products/{id}` | Shop owner | Update product |
| `PATCH` | `/api/products/{id}/active` | Shop owner | Toggle active/inactive |
| `DELETE` | `/api/products/{id}` | Shop owner | Delete product |

### Address

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/addresses` | Any authenticated | Add address (max 5) |
| `GET` | `/api/addresses` | Any authenticated | Get my addresses |
| `PUT` | `/api/addresses/{id}` | Address owner | Update address |
| `DELETE` | `/api/addresses/{id}` | Address owner | Delete address (auto-promote default) |
| `PATCH` | `/api/addresses/{id}/default` | Address owner | Set as default address |

### Cart

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/cart/items` | Any authenticated | Add product to cart |
| `GET` | `/api/cart` | Any authenticated | View cart |
| `PATCH` | `/api/cart/items/{id}` | Cart owner | Update item quantity |
| `DELETE` | `/api/cart/items/{id}` | Cart owner | Remove item |
| `DELETE` | `/api/cart` | Cart owner | Clear entire cart |

### Order

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/orders` | Any authenticated | Place order from cart |
| `GET` | `/api/orders` | Any authenticated | My orders (paginated) |
| `GET` | `/api/orders/{id}` | Customer or shop owner | Get order details |
| `PATCH` | `/api/orders/{id}/status` | SELLER (shop owner) | Update order status |
| `PATCH` | `/api/orders/{id}/cancel` | Customer (order owner) | Cancel order (PENDING only) |

**Total: 38 API endpoints**

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/gulshangupta003/FreshCo-Backend.git
cd FreshCo-Backend
```

2. **Set up MySQL**

```sql
-- MySQL will auto-create the database via the connection URL
-- Just ensure MySQL is running on port 3306
```

3. **Configure environment variables**

Create a `.env` file at the project root (or set via IntelliJ Run Configuration):

```properties
DB_URL=jdbc:mysql://localhost:3306/freshco_db?createDatabaseIfNotExist=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_256_bit_secret_key
JWT_EXPIRATION=86400000
```

4. **Run the application**

```bash
./mvnw spring-boot:run
```

5. **Import Postman collection**

Import `FreshCo-Backend.postman_collection.json` from the repository root into Postman. Set `seller_token` and `customer_token` variables after registration.

---

## 🔐 Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | MySQL connection URL | `jdbc:mysql://localhost:3306/freshco_db?...` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `root1234` |
| `JWT_SECRET` | JWT signing secret key | `default-secret-key-change-in-production` |
| `JWT_EXPIRATION` | Token expiry in milliseconds | `86400000` (24 hours) |
| `JPA_DDL_AUTO` | Hibernate DDL strategy | `update` |
| `JPA_SHOW_SQL` | Log SQL queries | `true` |
| `SERVER_PORT` | Application port | `8080` |

---

## 📁 Project Structure

```
src/main/java/com/freshco/
├── config/
│   └── SecurityConfig.java              # Security filter chain, CORS, session management
├── controller/
│   ├── AddressController.java           # Address CRUD endpoints
│   ├── AuthController.java              # Register, Login
│   ├── CartController.java              # Cart management endpoints
│   ├── CategoryController.java          # Category CRUD + nested products
│   ├── OrderController.java             # Order placement & management
│   ├── ProductController.java           # Product CRUD + search + toggle
│   ├── RoleController.java              # Role listing
│   ├── ShopController.java              # Shop CRUD + nested resources
│   └── UserController.java              # User profile
├── dto/
│   ├── request/                         # Request DTOs with validation
│   │   ├── AddToCartRequestDto.java
│   │   ├── AddressRequestDto.java
│   │   ├── CategoryRequestDto.java
│   │   ├── LoginRequestDto.java
│   │   ├── PlaceOrderRequestDto.java
│   │   ├── ProductRequestDto.java
│   │   ├── RegisterRequestDto.java
│   │   ├── ShopRequestDto.java
│   │   ├── UpdateCartItemQuantityRequestDto.java
│   │   └── UpdateOrderStatusRequestDto.java
│   └── response/                        # Response DTOs
│       ├── AddressResponseDto.java
│       ├── CartItemResponseDto.java
│       ├── CartResponseDto.java
│       ├── CategoryResponseDto.java
│       ├── OrderCountResponseDto.java
│       ├── OrderItemResponseDto.java
│       ├── OrderResponseDto.java
│       ├── PagedResponseDto.java
│       ├── ProductResponseDto.java
│       ├── RoleDto.java
│       ├── ShopResponseDto.java
│       └── UserDto.java
├── entity/
│   ├── Address.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Category.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderStatus.java
│   ├── PaymentMethod.java
│   ├── PaymentStatus.java
│   ├── Product.java
│   ├── Role.java
│   ├── Shop.java
│   └── User.java
├── exception/
│   ├── BadRequestException.java
│   ├── DuplicateResourceException.java
│   ├── GlobalExceptionHandler.java      # Centralized error handling (RFC 7807)
│   ├── JwtAuthenticationException.java
│   └── ResourceNotFoundException.java
├── repository/
│   ├── AddressRepository.java
│   ├── CartItemRepository.java
│   ├── CartRepository.java
│   ├── CategoryRepository.java
│   ├── OrderRepository.java
│   ├── ProductRepository.java
│   ├── ShopRepository.java
│   └── UserRepository.java
├── security/
│   ├── CustomAccessDeniedHandler.java   # Delegates 403 to GlobalExceptionHandler
│   ├── CustomAuthenticationEntryPoint.java # Delegates 401 to GlobalExceptionHandler
│   ├── CustomUserDetails.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   └── UserDetailsServiceImpl.java
├── service/
│   ├── impl/
│   │   ├── AddressServiceImpl.java
│   │   ├── AuthServiceImpl.java
│   │   ├── CartServiceImpl.java
│   │   ├── CategoryServiceImpl.java
│   │   ├── OrderServiceImpl.java
│   │   ├── ProductServiceImpl.java
│   │   ├── ShopServiceImpl.java
│   │   └── UserServiceImpl.java
│   ├── AddressService.java
│   ├── AuthService.java
│   ├── CartService.java
│   ├── CategoryService.java
│   ├── OrderService.java
│   ├── ProductService.java
│   ├── ShopService.java
│   └── UserService.java
└── FreshcoBackendApplication.java
```

---

## 💡 Key Design Decisions

| Decision | Rationale |
|---|---|
| **One seller = One shop** | Matches local grocery domain. Simplifies product/order APIs. Extensible to one-to-many if needed. |
| **Single-shop cart rule** | Prevents multi-shop delivery complexity. Cart stores `shop_id` for O(1) validation. |
| **Backend cart (not frontend)** | Persists across sessions and devices. Demonstrates more backend skills for resume. |
| **Lazy cart creation** | Cart created on first add, not at registration. Saves storage for users who never shop. |
| **Cart kept on clear (not deleted)** | Reuses cart row. Avoids repeated INSERT/DELETE cycles. `shop_id` set to null on clear. |
| **Non-idempotent delete for owned resources** | Shop, Product, Order return 404 on second delete. Owner should know the resource is gone. |
| **Idempotent delete for shared resources** | Category always returns 204. No ownership — consistent behavior. |
| **Pass user ID (not email) to services** | Primary key lookup is fastest. Services stay framework-agnostic. |
| **HandlerExceptionResolver delegation** | Security handlers delegate to GlobalExceptionHandler. Single source of truth for all errors. |
| **Price locking in OrderItem** | `unitPrice` saved at order time. Future price changes don't affect existing orders. |
| **Explicit column lengths** | VARCHAR sizes match actual data (e.g., pincode=6, mobileNumber=15). Reduces storage and improves indexes. |
| **Environment variable externalization** | Secrets use `${ENV_VAR:default}` pattern. Safe for GitHub, configurable in production. |

---

## 🚨 Error Handling

All errors follow **RFC 7807 ProblemDetail** format:

```json
{
    "type": "https://api.freshco.com/errors/not-found",
    "title": "Not Found",
    "status": 404,
    "detail": "Product not found with id: 99",
    "timestamp": "2026-03-10T12:00:00Z"
}
```

| Status | Type | When |
|---|---|---|
| `400` | Bad Request | Validation failure, business rule violation |
| `401` | Unauthorized | Missing or invalid JWT token |
| `403` | Forbidden | Insufficient role or not the resource owner |
| `404` | Not Found | Resource doesn't exist |
| `409` | Conflict | Duplicate resource (email, category name, etc.) |
| `500` | Internal Server Error | Unexpected errors |

---

## 📄 License

This project is licensed under the Apache License 2.0 — see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Gulshan Gupta**

- GitHub: [@gulshangupta003](https://github.com/gulshangupta003)