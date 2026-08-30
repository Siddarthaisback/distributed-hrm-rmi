# 🌐 Distributed Human Resource & Leave Management System (DCS)

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Java RMI](https://img.shields.io/badge/Architecture-Java_RMI-007396?logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/rmi/)
[![SSL / TLS](https://img.shields.io/badge/Security-SSL%20%2F%20TLS%20Sockets-4CAF50?logo=letsencrypt&logoColor=white)](https://en.wikipedia.org/wiki/Transport_Layer_Security)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

An enterprise distributed Human Resource Management (HRM) and leave processing system built with **Java RMI (Remote Method Invocation)**, **custom SSL/TLS socket factories**, **PostgreSQL**, and **Java Swing GUI**.

---

## 🏗️ Architecture & Component Design

The system implements a secure multi-tier distributed architecture where client applications interact with the central server across encrypted network channels:

```
Distributed HRM System
  ├── 🖥️ Server Layer (RMI Registry & SSL Listener)
  │   ├── HRMServer.java              # RMI registry bootstrap & socket binding
  │   ├── HRMServiceImpl.java          # Remote service implementation
  │   ├── ServerManager.java           # Server runtime & lifecycle manager
  │   └── ServerConsole.java           # Server administration & monitoring GUI
  │
  ├── 🔒 Security & Transport Layer
  │   ├── SSLConfig.java               # SSL Keystore / Truststore configuration
  │   ├── HRMServerSocketFactory.java  # Custom SSL RMIServerSocketFactory
  │   ├── HRMClientSocketFactory.java  # Custom SSL RMIClientSocketFactory
  │   └── PasswordUtil.java            # PBKDF2 / SHA-256 password hashing with salt
  │
  ├── 📡 Remote Interfaces & DTOs
  │   ├── HRMService.java              # Remote RMI interface contract
  │   ├── Employee.java                # Serializable employee data model
  │   ├── LeaveRequest.java            # Leave application & status model
  │   ├── FamilyDetails.java           # Employee family records
  │   └── YearlyEmployeeReport.java    # Aggregated leave analytics model
  │
  ├── 🗄️ Database & DAO Layer
  │   ├── DBConnection.java            # PostgreSQL JDBC connection manager
  │   ├── EmployeeDAO.java             # Employee CRUD operations
  │   ├── LeaveDAO.java                # Leave quota & request transactions
  │   ├── FamilyDAO.java               # Dependent management DAO
  │   └── sql.sql                      # Relational schema (DDL, FKs, indexes)
  │
  └── 💻 Client Layer (Java Swing)
      ├── HRMClient.java               # RMI Client lookup & connection manager
      ├── LoginUI.java                 # Role-based authentication interface
      ├── HRDashboard.java             # HR Administrator portal
      └── EmployeeDashboard.java       # Employee self-service portal
```

---

## ✨ Features

- 🔐 **End-to-End Encrypted RMI**: Custom `RMIClientSocketFactory` and `RMIServerSocketFactory` ensuring all RPC traffic is encrypted via SSL/TLS.
- 👥 **Role-Based Access Control (RBAC)**:
  - **HR Administrator**: Register employees, approve/reject leave requests, view company-wide staff rosters, generate annual leave reports.
  - **Employee**: Apply for annual/medical leaves, view real-time leave balances, manage family details, update profiles.
- 📊 **Real-Time Leave Calculation**: Automatic quota deduction, remaining days tracking, and balance ledger consistency.
- 🗄️ **PostgreSQL Persistence**: Fully normalized schema with index-optimized lookups (`idx_employee_email`, `idx_leave_employee`) and cascade referential integrity.

---

## 🚀 Quick Start Guide

### Prerequisites
- [Java Development Kit (JDK) 21+](https://openjdk.org/)
- [Apache Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)

### 1. Database Setup
Create your PostgreSQL database and load the schema from `sql.sql`:
```bash
psql -U postgres -d postgres -f sql.sql
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Start the RMI Server
```bash
mvn exec:java -Dexec.mainClass="org.example.server.HRMServer"
```

### 4. Launch the Client GUI
```bash
mvn exec:java -Dexec.mainClass="org.example.client.HRMClient"
```

---

## 📄 License & Attribution
Developed for the Distributed Computing Systems (CT075-3-3-DCS) coursework at Asia Pacific University (APU).
