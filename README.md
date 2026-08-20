# 🌐 NexusSoft : Enterprise Cloud SaaS & License Orchestration Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%20%2F%2025-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.1.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3" />
  <img src="https://img.shields.io/badge/Spring%20Security-6.0-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/JWT-HS256%20Stateless-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/AI-NexusAI%20Copilot-8A2BE2?style=for-the-badge&logo=openai&logoColor=white" alt="NexusAI" />
</p>

---

## 📌 Abstract & Overview

**NexusSoft** is a full-stack enterprise cloud application engineered to distribute software suites, provision SaaS licenses, manage multi-tenant billing, and deliver automated business intelligence.

Built on **Spring Boot 3.1.3, Spring Security 6, and MySQL**, the platform implements **stateless JSON Web Token (JWT) cryptographic authentication with BCrypt hashing** and integrates **NexusAI Copilot** for conversational cloud advisory and predictive revenue telemetry.

---

## ✨ Key Platform Features

### 1. 🛡️ Enterprise Security & Stateless JWT Authentication
- **HMAC-SHA256 (HS256) Signed Tokens**: Secure token generation containing subject claims, display names, and authority roles (`ROLE_USER`, `ROLE_ADMIN`).
- **Dual-Channel Delivery**:
  - `HttpOnly` Secure Cookie (`jwt_token`) for seamless server-side page navigation without XSS vulnerability.
  - `Authorization: Bearer <token>` for REST API clients and microservices.
- **BCrypt Password Encryption**: All tenant and admin passwords in MySQL are hashed with 10 adaptive salt rounds (`$2a$10$...`).
- **Role-Based Access Control (RBAC)**: Strict separation between **Enterprise Client Workspaces** and **Cloud Ops Admin Consoles**.

### 2. 🤖 NexusAI Intelligent Copilot & Predictive Telemetry
- **Floating Interactive Assistant Widget**: Available across client pages with typing animation and quick prompt chips.
- **Dynamic Cost & Volume Discount Calculator**: Computes per-seat software pricing and automatically applies team (10%) and enterprise (20%) discount tiers.
- **Architecture Advisory**: Recommends customized software bundles based on business vertical (Fintech, Startup, Healthcare, Enterprise).
- **Admin ARR Forecasting**: Analyzes order velocity to project 30-day recurring revenue, identifies top-performing suites, and generates strategic recommendations.

### 3. 📦 Software Catalog & License Provisioning
- **Per-Seat License Model**: Dynamic license quantity calculation for software suites:
  - 🌐 *Nexus Cloud ERP Suite*
  - 🤖 *AI Vision & Analytics Engine*
  - 🛡️ *CyberShield Enterprise Security*
  - ⚡ *DevOps CI/CD Automation Hub*
  - 📊 *OmniChannel CRM & Helpdesk*
  - 🔗 *Microservices API Gateway Pro*
- **Client Workspace**: Instant catalog search, live seat allocation, and active subscription invoice ledger.

### 4. 🎛️ Cloud Operations Admin Console
- **Executive KPI Dashboard**: Real-time counters for *Client Accounts*, *Catalog Suites*, *Active Subscriptions*, and *DevOps Admins*.
- **Modular Data Tables**: Full CRUD capabilities for enterprise tenants, catalog modules, invoices, and system administrators.

---

## 🏛️ System Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                              │
│   Thymeleaf 3 + HTML5 + CSS3 (Glassmorphism, Mobile Responsive)        │
│   NexusAI Copilot UI Widget + Chart.js Telemetry UI                    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP/HTTPS (Cookie + Bearer Token)
┌───────────────────────────────────▼────────────────────────────────────┐
│                    SECURITY & INTERCEPTOR LAYER                        │
│   JwtAuthenticationFilter (HS256) ──► Spring Security 6 (Stateless)    │
│   BCrypt Password Encryption (10 Salt Rounds) ──► RBAC Engine          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                        CONTROLLER & API LAYER                          │
│   HomeController ──► AdminController ──► AiController                 │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                         SERVICE / BUSINESS LOGIC                       │
│   AiService ──► ProductServices ──► OrderServices ──► UserServices     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                     DATA ACCESS LAYER (Hibernate JPA)                  │
│   ProductRepository ──► OrderRepository ──► UserRepository             │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ JDBC / HikariCP Pool
┌───────────────────────────────────▼────────────────────────────────────┐
│                       PERSISTENCE LAYER (MySQL 8)                      │
│   Tables: `user`, `admin`, `product`, `orders`                         │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Backend Framework** | Spring Boot 3.1.3 (Java 17 / 25) |
| **Security & Auth** | Spring Security 6.0, JJWT (Java JWT 0.11.5), BCrypt |
| **Artificial Intelligence** | NexusAI Natural Language Engine, Rule & Graph Heuristics |
| **ORM / Data Access** | Spring Data JPA, Hibernate ORM, HikariCP |
| **Database** | MySQL 8.0 / MySQL 9.0 |
| **Frontend / UI** | Thymeleaf 3, Vanilla CSS3 (Custom Design System), JavaScript, FontAwesome 6 |
| **Build & Tooling** | Maven 3.9.4, Maven Wrapper (`mvnw`), Docker |

---

## 🚀 Getting Started & Local Installation

### 1. Prerequisites
- **JDK 17** or higher installed (`java -version`)
- **MySQL 8.0+** running locally on port `3306`

### 2. Clone the Repository
```bash
git clone https://github.com/<your-username>/NexusSoft.git
cd NexusSoft
```

### 3. Configure Database Credentials
Edit `src/main/resources/application.properties`:
```properties
server.port=2330
spring.datasource.url=jdbc:mysql://localhost:3306/businessproject
spring.datasource.username=root
spring.datasource.password=Root1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

### 4. Build and Run Application
```bash
# Using Maven Wrapper
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

The application will start on: **[http://localhost:2330/](http://localhost:2330/)**

---

## 🧪 Demo Credentials & Key Routes

| Portal / Feature | URL Route | Demo Credentials | Access Role |
|---|---|---|---|
| 🌐 **Public SaaS Landing** | `/` | Open Access | Public |
| 📦 **Software Solutions** | `/products` | Open Access | Public |
| 🤖 **NexusAI Assistant** | Floating Widget | Open Access | Public / User |
| 🔐 **Sign In / Sign Up** | `/login`, `/register` | Open Access | Public |
| 💻 **Client Workspace** | `/product/back` | Register or Login | `ROLE_USER` |
| 🎛️ **Cloud Ops Console** | `/admin/services` | `kiranmk628@gmail.com` / `1234` | `ROLE_ADMIN` |

---

## 📡 REST API Reference

### 1. AI Chat Copilot
```http
POST /api/ai/chat
Content-Type: application/json

{
  "message": "Estimate cost for 50 seats of Cloud ERP"
}
```
**Response:**
```json
{
  "reply": "### 💰 Cost Estimation for Nexus Cloud ERP Suite...",
  "suggestions": [
    "Deploy Nexus Cloud ERP Suite",
    "Compare with other suites"
  ]
}
```

### 2. Admin AI ARR Forecast & Insights
```http
GET /api/ai/insights
```
**Response:**
```json
{
  "projectedMonthlyRevenue": "₹ 48600.00",
  "estimatedGrowthPercent": "+24.0%",
  "topTrendingModule": "Nexus Cloud ERP Suite",
  "activeTenants": 1,
  "recommendations": [
    "High demand detected for Nexus Cloud ERP Suite — bundle with CyberShield Security for a 15% ARR boost."
  ]
}
```

---

## ☁️ Deployment & CI/CD Pipeline

The project includes ready-to-deploy configuration for cloud platforms (Render, Railway, AWS EC2):

1. **Docker Container**: Build with `docker build -t nexussoft .` and run with `docker run -p 2330:2330 nexussoft`.
2. **GitHub Actions CI/CD**: Automatic build, unit testing, and Docker packaging on every `git push origin main`.
3. **Webhook Continuous Deployment**: Zero-downtime auto-redeploy when new commits arrive.

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
