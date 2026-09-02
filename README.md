# 🏥 MediPulse Pro : Enterprise Hospital Inventory & Consumable Expiry Predictor

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%20%2F%2025-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17/25" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.1.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3" />
  <img src="https://img.shields.io/badge/Spring%20Security-6.0-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/JWT-HS256%20Stateless-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/AI-MediPulse%20Copilot-0284C7?style=for-the-badge&logo=openai&logoColor=white" alt="MediPulse AI" />
</p>

---

## 📌 Abstract & Clinical Problem

Hospitals routinely tie up millions in dead stock while simultaneously facing life-threatening stockouts of acute pharmaceuticals, surgical implants, and anesthesia agents. 

**MediPulse Pro** is an enterprise hospital inventory and consumable replenishment platform engineered to achieve:
1. **Zero Clinical Stockouts (99.98% Availability SLA)** through statistical Reorder Point ($ROP$) and Safety Stock ($SS$) variance math.
2. **Zero Expired Consumable Waste (> 40% Loss Reduction)** via algorithmic **First-Expire-First-Out (FEFO)** batch allocation, real-time **Expiry Radar** telemetry, and automated **Inter-Ward Stock Transfers**.

---

## 🧮 Mathematical & Supply-Chain Algorithms

### 1. Reorder Point ($ROP$) with Lead-Time Variability
$$ROP = (d \times L) + SS$$
- $d$: Average daily consumable burn rate in units.
- $L$: Supplier delivery lead time in days.
- $SS$: Safety Stock Buffer:
$$SS = Z \times \sqrt{L \times \sigma_d^2 + d^2 \times \sigma_L^2}$$
*(Evaluated at $Z = 2.33$ for a 99% clinical uptime SLA).*

### 2. Economic Order Quantity ($EOQ$)
$$EOQ = \sqrt{\frac{2 \times D \times S}{H}}$$
- $D$: Annual consumable demand.
- $S$: Fixed administrative cost per purchase order.
- $H$: Holding cost per unit per year (cold-chain refrigeration, insurance, obsolescence).

### 3. Expiry Risk Score Index (ERSI) & FEFO Dispatch
Each inbound batch is assigned an ERSI score ($0 - 100$):
$$ERSI = \max\left(0, \; 100 \times \left(1 - \frac{\text{Days to Expiry}}{\text{Batch Quantity} / d}\right)\right)$$
If $ERSI > 75$, the consumable cannot be consumed at the current department's velocity before expiration. The platform automatically triggers an **Inter-Ward Stock Transfer** to high-velocity units (e.g. from Ortho Ward to Emergency Trauma OT).

---

## 🏛️ System Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                              │
│   Thymeleaf 3 + Glassmorphism CSS3 + MediPulse AI Widget               │
│   Clinical Expiry Radar + Stockout Alert Tables + Modal Workflows      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP/HTTPS (JWT Cookie + Bearer)
┌───────────────────────────────────▼────────────────────────────────────┐
│                    SECURITY & INTERCEPTOR LAYER                        │
│   JwtAuthenticationFilter (HS256) ──► Spring Security 6 (Stateless)    │
│   BCrypt Password Hashing (10 Rounds) ──► Hospital RBAC                │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                        CONTROLLER & REST LAYER                         │
│   HospitalInventoryController ──► AdminController ──► AiController     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                       CLINICAL SERVICE & AI LAYER                      │
│   HospitalInventoryService ──► AiService (Clinical Copilot)            │
│   FEFO Dispatch Engine ──► ROP & EOQ Calculator ──► Inter-Ward Swap    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                     DATA ACCESS LAYER (Hibernate JPA)                  │
│   HospitalDepartmentRepository ──► InventoryBatchRepository            │
│   RequisitionRepository ──► ProductRepository ──► UserRepository       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ MySQL 8 / HikariCP Pool
┌───────────────────────────────────▼────────────────────────────────────┐
│                       PERSISTENCE LAYER (MySQL 8)                      │
│   Tables: hospital_department, inventory_batch, requisitions,         │
│           requisition_items, product, user, admin                      │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🧪 Demo Credentials & Key Routes

| Portal / Feature | URL Route | Demo Credentials | Role |
|---|---|---|---|
| 🌐 **Public Overview & SLA** | `/` | Open Access | Public |
| 💊 **Medical Master Formulary** | `/products` | Open Access | Public / Clinical |
| 🤖 **MediPulse AI Assistant** | Floating Widget | Open Access | All |
| 🔐 **Staff Login** | `/login` | `kiranmk628@gmail.com` / `1234` | `ROLE_ADMIN` |
| 🎛️ **Clinical Command Console** | `/admin/services` | Login required | `ROLE_ADMIN` |

---

## 📡 REST API Reference

### 1. Expiry Radar & Capital at Risk Telemetry
```http
GET /api/hospital/expiry-radar?daysThreshold=60
```
**Response:**
```json
{
  "criticalCount": 2,
  "nearExpiryCount": 1,
  "totalCapitalAtRisk": 64050.0,
  "criticalBatches": [
    {
      "batchId": 1,
      "batchNumber": "MERO-2026-B88",
      "itemName": "Meropenem 1g IV Infusion",
      "department": "Central Hospital Pharmacy",
      "daysLeft": 18,
      "quantity": 37,
      "capitalAtRisk": 31450.0
    }
  ],
  "transferRecommendations": [
    {
      "batchNumber": "MERO-2026-B88",
      "itemName": "Meropenem 1g IV Infusion",
      "fromDepartment": "Central Hospital Pharmacy",
      "toDepartment": "Emergency OT / ICU",
      "reason": "Batch expires in 18 days. High daily patient throughput in Emergency OT can consume this before expiry."
    }
  ]
}
```

### 2. Stockout & ROP Diagnostics
```http
GET /api/hospital/stockout-alerts
```

### 3. Inter-Ward Stock Transfer
```http
POST /api/hospital/transfer
Content-Type: application/json

{
  "batchId": 1,
  "targetDepartmentId": 1,
  "quantity": 5
}
```

### 4. Register Inbound Batch Lot
```http
POST /api/hospital/batch/add
Content-Type: application/json

{
  "productId": 1,
  "departmentId": 3,
  "batchNumber": "MERO-2026-B99",
  "expiryDate": "2027-05-15",
  "quantity": 50,
  "purchaseCost": 850.00
}
```

---

## 🚀 Running Locally

```bash
# 1. Compile Java sources
./mvnw clean compile -DskipTests

# 2. Start Application
./mvnw spring-boot:run
```

App runs on: **[http://localhost:2330/](http://localhost:2330/)**
