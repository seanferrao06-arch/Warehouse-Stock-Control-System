# 📦 Warehouse Stock Control System

A role-based web application for day-to-day warehouse inventory management, built with **Spring Boot 3.4**, **Thymeleaf**, **Spring Security 6** and **pure JDBC** (no JPA/Hibernate) on **MySQL 8**.

🔗 **Live demo:** https://warehouse-stock-control-system.onrender.com/login
> Hosted on a free tier, so the first load can take up to a minute while the server wakes up.

<!-- Add 3–4 screenshots here: login, dashboard, stock in/out, reports -->
<!-- ![Dashboard](docs/screenshots/dashboard.png) -->

---

## ✨ Features

- **Role-based access** – `ADMIN` (full system) and `STAFF` (dashboard + stock operations only). Restrictions are enforced server-side with Spring Security and `@PreAuthorize`, not just hidden in the UI.
- **Atomic stock operations** – Stock IN / OUT update the balance and write to the movement log in a single transaction; overdrafts are rejected.
- **Immutable audit trail** – every movement is logged append-only.
- **Low-stock alerts** – visual warnings when quantity falls below a product's reorder level.
- **Reports & analytics** – date-filtered movement history, stock valuation (₹), per-product net flow.
- **Master data management** – products, categories, suppliers, warehouse locations, users.

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17+, Spring Boot 3.4, Spring Security 6 |
| Data access | Pure JDBC (`JdbcTemplate`), MySQL 8 |
| Frontend | Thymeleaf, Bootstrap 5, Bootstrap Icons |
| Testing | JUnit (security routes, stock operations, password util, models) |
| Deployment | Docker, Render |

## 🏛️ Architecture

```text
Browser (Thymeleaf / Bootstrap 5)
  └─► Controller layer (@Controller + @PreAuthorize + validation)
        └─► Service layer (@Service)
              └─► DAO layer (@Repository, JdbcTemplate)
                    └─► MySQL 8 (warehouse_stock)
```

Five custom interfaces in `warehouse.interfaces` define the contracts implemented by the DAOs:

| Interface | Implemented by | Responsibility |
|---|---|---|
| `Crud<T>` | Product, Category, Supplier, Location, User, Stock DAOs | Persistence and retrieval |
| `StockManageable` | `StockDAO` | Atomic stock in / out |
| `Reportable` | `ReportDAO` | Low-stock, movement and valuation reports |
| `Authenticatable` | `UserDAO` | Authentication contract |
| `Auditable` | `StockDAO` | Append-only movement logging |

## 🚀 Run Locally

**Prerequisites:** Java 17+, Maven 3.9+, MySQL 8.x

```bash
# 1. Clone
git clone https://github.com/<your-username>/Warehouse-Stock-Control-System.git
cd Warehouse-Stock-Control-System

# 2. Create the database and seed demo data
mysql -u root -p < schema.sql

# 3. Configure credentials via environment variables (never commit them)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=warehouse_stock
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password

# 4. Run
mvn spring-boot:run
```

Open http://localhost:8080 (or the port set in `PORT`).
See [`.env.example`](.env.example) for all supported variables.

## 🔐 Demo Accounts

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `staff` | `staff123` | STAFF |

These are **public demo credentials for seeded sample data only**. Do not enter real or personal information in the live demo.

## 🧪 Tests

```bash
mvn clean test
```

## 🛡️ Security Notes & Roadmap

Being upfront about what this project does and what is still on the roadmap:

- ✅ Server-side role enforcement on every protected route
- ✅ Parameterised queries via `JdbcTemplate`
- ✅ Secrets read from environment variables, not committed
- 🔜 Replace unsalted SHA-256 password hashing with **BCrypt** (or Argon2)
- 🔜 Add login rate-limiting / account lockout
- 🔜 Disable one-click demo credentials and add data reset for the public demo
- 🔜 CI pipeline (GitHub Actions) running `mvn test` on every push

## 👥 Team

- **Sean** – SYBSc Computer Science
- **Devan** – SYBSc Computer Science

Built as a Java academic project at Bhavan's College (Autonomous), Mumbai.

## 📄 License

Released under the MIT License (see `LICENSE`).