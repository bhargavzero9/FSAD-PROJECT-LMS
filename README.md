# FSAD-PROJECT-LMS

A full-stack **Learning Management System (LMS)** — *Digital Blackboard* — built with a **Spring Boot** backend and a **React (Vite)** frontend.

---

## 🏗️ Tech Stack

| Layer     | Technology                              |
|-----------|----------------------------------------|
| Frontend  | React 19, Vite, Axios, React Router    |
| Backend   | Spring Boot 3.2, Spring Data JPA, Lombok |
| Database  | MySQL 8                                |
| Email     | Spring Mail (Gmail SMTP)               |

---

## 📁 Project Structure

```
FSAD-PROJECT-LMS/
├── backend-spring/          # Spring Boot REST API
│   ├── src/main/java/com/dbb/
│   │   ├── config/          # CORS, app configuration
│   │   ├── controller/      # REST controllers
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   └── DigitalBlackBoardApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql         # Seed data
│   └── pom.xml
├── frontend/                # React Vite app
│   ├── src/
│   │   ├── components/      # Reusable UI components
│   │   ├── context/         # React context providers
│   │   ├── pages/           # Route pages
│   │   └── utils/           # API service layer
│   ├── index.html
│   └── vite.config.js
└── README.md
```

---

## ⚙️ Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **Node.js 18+** & npm
- **MySQL 8** running on `localhost:3306`

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/bhargavzero9/FSAD-PROJECT-LMS.git
cd FSAD-PROJECT-LMS
```

### 2. Configure the database

Edit `backend-spring/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dbb_lms_gmail?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

> The database `dbb_lms_gmail` will be created automatically on first run.

### 3. Start the backend

```bash
cd backend-spring
./mvnw.cmd spring-boot:run      # Windows
# or
./mvnw spring-boot:run          # macOS / Linux
```

Backend starts at **http://localhost:5000**

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts at **http://localhost:5174**

---

## 📬 Email Verification

The LMS supports real email verification via Gmail SMTP. Configure your app password in `application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

---

## 👥 Default Roles

| Role             | Description                        |
|------------------|------------------------------------|
| ADMIN            | Full platform management           |
| CONTENT_CREATOR  | Create and manage courses/content  |
| STUDENT          | Enroll in courses, submit work     |

---

## 📄 License

This project is for academic/educational purposes (FSAD coursework).
