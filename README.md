<p align="center">
  <img src="https://img.shields.io/badge/%F0%9F%8D%83_FreshCo-E--commerce_Backend-00C853?style=for-the-badge&labelColor=1B5E20" alt="FreshCo" />
</p>

<p align="center">
  <em>A production-grade e-commerce REST API connecting local vegetable & fruit shopkeepers with nearby customers</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Security-7.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Hibernate-7.x-59666C?style=for-the-badge&logo=hibernate&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-0.13-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
  <img src="https://img.shields.io/badge/Swagger-OpenAPI_3.1-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" />
  <img src="https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/License-Apache_2.0-D22128?style=for-the-badge&logo=apache&logoColor=white" />
</p>

<p align="center">
  <a href="#-highlights">Highlights</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-api-reference-45-endpoints">API Reference</a> •
  <a href="#-quick-start">Quick Start</a> •
  <a href="#-design-decisions">Design Decisions</a>
</p>

---

## 🎯 Highlights

<table>
<tr>
<td width="50%">

**🔐 Security**
- JWT stateless auth with Spring Security
- Email verification via 6-digit OTP
- Password reset with expiring tokens
- Account lockout after failed attempts
- Strong password policy enforcement
- RFC 7807 ProblemDetail error format

</td>
<td width="50%">

**🛒 E-commerce**
- Location-based shop discovery (pincode)
- Single-shop cart enforcement
- Price locking at order time
- Validated order status workflow
- Auto stock management
- Max 5 addresses with smart defaults

</td>
</tr>
</table>

**45 API endpoints** · **10 database tables** · **9 modules** · **3 roles** (Admin, Seller, Customer)

---

## 🏗 Architecture

```
                         ┌─────────────────────┐
                         │   Client / Postman   │
                         └──────────┬──────────┘
                                    │ HTTP
                         ┌──────────▼──────────┐
                         │  JWT Auth Filter     │
                         │  (validate token)    │
                         └──────────┬──────────┘
                                    │
                         ┌──────────▼──────────┐
                         │  Controller Layer    │
                         │  @PreAuthorize       │
                         │  @Valid + DTOs       │
                         └──────────┬──────────┘
                                    │
                         ┌──────────▼──────────┐
                         │  Service Layer       │
                         │  Business logic      │
                         │  Ownership checks    │
                         │  @Transactional      │
                         └──────────┬──────────┘
                                    │
                         ┌──────────▼──────────┐
                         │  Repository Layer    │
                         │  Spring Data JPA     │
                         └──────────┬──────────┘
                                    │
                         ┌──────────▼──────────┐
                         │  MySQL 8 (10 tables) │
                         └─────────────────────┘
```

### Error Handling — Single Source of Truth

All exceptions (including Spring Security 401/403) are routed through `GlobalExceptionHandler` using the **HandlerExceptionResolver delegation pattern**. Every error response follows **RFC 7807 ProblemDetail**.

```json
{
   "type": "https://api.freshco.com/errors/not-found",
   "title": "Resource Not Found",
   "status": 404,
   "detail": "Product not found with id: 99",
   "timestamp": "2026-03-23T10:00:00Z"
}
```

---

## 🗄 Database Schema

```
users ─────── 1:1 ─── shops ───── 1:N ─── products ──── N:1 ─── categories
  │                     │                      │
  │ 1:1                 │ 1:N                  │
  │                     │                      │
 carts ── 1:N ── cart_items                    │
  │                                            │
  │ 1:N                                        │
  │                                            │
orders ── 1:N ── order_items ──── N:1 ─────────┘
  │
  │ N:1
  │
addresses            password_reset_tokens ── N:1 ── users
```

All VARCHAR columns use **explicit lengths** (not default 255) — e.g. `pincode=6`, `phone=15`, `email=100`, `bcrypt=72`.

---

## 📡 API Reference (45 endpoints)

### 1. Auth — 6 endpoints

| Method | Endpoint | Description |
|:------:|----------|-------------|
| `POST` | `/api/auth/register` | Register + sends verification OTP |
| `POST` | `/api/auth/login` | Login (requires verified email) |
| `POST` | `/api/auth/verify-email` | Verify email with 6-digit OTP |
| `POST` | `/api/auth/resend-otp` | Resend verification OTP |
| `POST` | `/api/auth/forgot-password` | Send password reset token |
| `POST` | `/api/auth/reset-password` | Reset password with token |

### 2. Roles — 2 endpoints

| Method | Endpoint | Description |
|:------:|----------|-------------|
| `GET` | `/api/roles` | All roles with codes |
| `GET` | `/api/roles/map` | Roles as key-value map |

### 3. User — 1 endpoint

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| `GET` | `/api/users/me` | Auth | My profile |

### 4. Shop — 10 endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| `POST` | `/api/shops` | Seller | Create shop (one per seller) |
| `GET` | `/api/shops/me` | Seller | My shop |
| `GET` | `/api/shops/me/orders/count` | Seller | Dashboard order stats |
| `GET` | `/api/shops` | Auth | All shops |
| `GET` | `/api/shops/pincode/{pincode}` | Auth | Nearby shops (paginated) |
| `GET` | `/api/shops/{id}` | Auth | Shop by ID |
| `PUT` | `/api/shops/{id}` | Owner | Update shop |
| `DELETE` | `/api/shops/{id}` | Owner | Delete shop |
| `GET` | `/api/shops/{shopId}/products` | Auth | Shop products (paginated) |
| `GET` | `/api/shops/{shopId}/orders` | Owner | Shop orders (paginated) |

### 5. Category — 6 endpoints

| Method | Endpoint | Description |
|:------:|----------|-------------|
| `POST` | `/api/categories` | Create category |
| `GET` | `/api/categories` | All categories |
| `GET` | `/api/categories/{id}` | Category by ID |
| `PUT` | `/api/categories/{id}` | Update category |
| `DELETE` | `/api/categories/{id}` | Delete (idempotent) |
| `GET` | `/api/categories/{id}/products` | Category products (paginated) |

### 6. Product — 7 endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| `POST` | `/api/products` | Seller | Create (shop auto-linked) |
| `GET` | `/api/products/search?q=` | Auth | Search by name (paginated) |
| `GET` | `/api/products` | Auth | All products (paginated) |
| `GET` | `/api/products/{id}` | Auth | Product by ID |
| `PUT` | `/api/products/{id}` | Owner | Update product |
| `PATCH` | `/api/products/{id}/active` | Owner | Toggle active/inactive |
| `DELETE` | `/api/products/{id}` | Owner | Delete product |

### 7. Address — 5 endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| `POST` | `/api/addresses` | Auth | Add (max 5, first = default) |
| `GET` | `/api/addresses` | Auth | My addresses |
| `PUT` | `/api/addresses/{id}` | Owner | Update address |
| `DELETE` | `/api/addresses/{id}` | Owner | Delete (auto-promote default) |
| `PATCH` | `/api/addresses/{id}/default` | Owner | Set default |

### 8. Cart — 5 endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| `POST` | `/api/cart/items` | Auth | Add to cart (single-shop rule) |
| `GET` | `/api/cart` | Auth | View cart |
| `PATCH` | `/api/cart/items/{id}` | Owner | Update quantity |
| `DELETE` | `/api/cart/items/{id}` | Owner | Remove item |
| `DELETE` | `/api/cart` | Owner | Clear cart |

### 9. Order — 5 endpoints

| Method | Endpoint | Access | Description |
|:------:|----------|--------|-------------|
| `POST` | `/api/orders` | Auth | Place order (price lock + stock) |
| `GET` | `/api/orders` | Auth | My orders (paginated) |
| `GET` | `/api/orders/{id}` | Customer/Seller | Order details |
| `PATCH` | `/api/orders/{id}/status` | Seller | Advance status |
| `PATCH` | `/api/orders/{id}/cancel` | Customer | Cancel (PENDING only) |

### Order Status Workflow

```
PENDING ──→ CONFIRMED ──→ PROCESSING ──→ OUT_FOR_DELIVERY ──→ DELIVERED
   │             │                                                 │
   │             │                                            PAID (COD)
   ▼             ▼
CANCELED     CANCELED
(customer)   (seller)
```

---

## 🚀 Quick Start

### Prerequisites

```
Java 21+  •  Maven 3.9+  •  MySQL 8.0+  •  Gmail App Password
```

### Setup

```bash
git clone https://github.com/gulshangupta003/FreshCo-Backend.git
cd FreshCo-Backend
```

Create `.env` at project root:

```properties
DB_URL=jdbc:mysql://localhost:3306/freshco_db?createDatabaseIfNotExist=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_256_bit_secret_key
JWT_EXPIRATION=86400000
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_16_char_app_password
SERVER_PORT=8080
```

> **Gmail App Password:** [Google Account](https://myaccount.google.com) → Security → 2-Step Verification → App Passwords

### Run

```bash
./mvnw spring-boot:run
```

### Explore

| Tool | URL |
|------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Postman | Import `FreshCo-Backend.postman_collection.json` |

---

## 📁 Project Structure

```
src/main/java/com/freshco/
├── config/            SecurityConfig, SwaggerConfig
├── controller/        9 REST controllers
├── dto/
│   ├── request/       13 request DTOs with validation
│   └── response/      12 response DTOs
├── entity/            10 JPA entities + 4 enums
├── exception/         Custom exceptions + GlobalExceptionHandler (RFC 7807)
├── repository/        10 Spring Data JPA repositories
├── security/          JWT filter, UserDetails, EntryPoint, AccessDenied
└── service/
    ├── impl/          10 service implementations
    └── *.java         Service interfaces
```

---

## 🔐 Security Features

| Feature | Implementation |
|---------|----------------|
| **Authentication** | JWT via `Authorization: Bearer <token>` |
| **Password storage** | BCrypt (strength 12) |
| **Password policy** | Min 8 chars: uppercase + lowercase + digit + special |
| **Email verification** | 6-digit OTP via Gmail SMTP (10 min expiry) |
| **Password reset** | UUID token via email (15 min expiry, one-time use) |
| **Account lockout** | 5 failed attempts → 15 min lock (DB-persisted) |
| **Role enforcement** | `@PreAuthorize` + service-layer ownership checks |
| **Error masking** | Same message for wrong email vs wrong password |

---

## 💡 Design Decisions

| Decision | Why |
|----------|-----|
| **One seller = One shop** | Matches local grocery domain. Extensible to 1:N later. |
| **Single-shop cart** | Cart stores `shop_id` — O(1) validation, no multi-shop delivery complexity. |
| **Backend persistent cart** | Survives sessions/devices. Amazon/Flipkart pattern. |
| **Lazy cart creation** | Created on first item add, not at registration. |
| **Cart kept on clear** | Reuses row. `shop_id` nulled instead of DELETE+INSERT. |
| **Price locking** | `unitPrice` in `order_items` — immune to future price changes. |
| **Validated status transitions** | Enum ordinal comparison — no skipping, no backward. |
| **HandlerExceptionResolver delegation** | Security 401/403 → same GlobalExceptionHandler → unified RFC 7807. |
| **OTP in user row** | No separate table. Old OTP overwritten on resend. |
| **DB-based login throttling** | Persists across app restarts. Can't bypass by restarting server. |
| **Explicit column lengths** | `pincode=6`, `phone=15`, `email=100` — not default 255. |
| **Idempotent DELETE (shared)** | Category → always 204. No owner = consistent behavior. |
| **Non-idempotent DELETE (owned)** | Shop/Product → 404 on re-delete. Owner should know it's gone. |
| **User ID passed to services** | PK lookup > email lookup. Services stay framework-agnostic. |
| **Environment externalization** | `${ENV_VAR}` in YAML. No secrets in code or Git. |

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Java (LTS) | 21 |
| Framework | Spring Boot | 4.0.2 |
| Security | Spring Security + JWT (jjwt) | 7.x / 0.13 |
| ORM | Spring Data JPA + Hibernate | 4.x / 7.x |
| Database | MySQL | 8.0 |
| API Docs | SpringDoc OpenAPI + Swagger UI | 3.0.2 |
| Email | Spring Mail (Gmail SMTP) | 4.x |
| Validation | Jakarta Bean Validation | 4.x |
| Build | Maven | 3.9 |
| Boilerplate | Lombok | 1.18 |

---

## 📄 License

Licensed under [Apache License 2.0](LICENSE).

---

<p align="center">
  Built with ☕ by <a href="https://github.com/gulshangupta003"><b>Gulshan Gupta</b></a>
</p>