# 🧠 MentorLink Backend

### 🚀 Intelligent Project Mentorship Management System

The **MentorLink Backend** is a **Spring Boot + MySQL + JWT** powered application that manages the complete workflow of student–faculty project mentorship — from registration and authentication to project creation, group formation, and faculty assignment.

This backend provides secure REST APIs for **students, faculty, and administrators**, integrating both traditional database operations and intelligent recommendation modules.

---


## 📚 Table of Contents

- Overview
- Key Features
- Tech Stack
- System Architecture
- API Modules
- Database Design
- Excel → MySQL Integration
- Authentication Flow
- Installation & Setup
- Testing with Postman
- Recommender System (Python)
- Future Enhancements

---

## 💡 Overview

MentorLink aims to simplify and automate the project mentoring process in universities and colleges. It allows students to register, form groups, submit projects, faculty to mentor and evaluate them, and admins to manage the entire ecosystem. The system uses JWT for secure authentication and MySQL for persistent storage.

---

## ⚙️ Key Features

- User management (Student, Faculty, Admin)
- JWT authentication
- Project creation and management
- Group formation with join token
- Faculty assignment
- Intelligent recommendation system using NLP
- Excel → MySQL bulk data upload
- Clean, RESTful Spring Boot APIs

---

## 🛠 Tech Stack

- **Backend:** Spring Boot (Java 17), Maven
- **Database:** MySQL
- **Security:** Spring Security + JWT
- **ORM:** Hibernate / JPA
- **Recommender:** Python (Sentence Transformers + Cosine Similarity)
- **Data Handling:** Pandas & SQLAlchemy

---

## 🧩 System Architecture

```
Frontend (Flutter/React)
          │
          ▼
   Spring Boot Backend
          │
          ▼
        MySQL
          │
          ▼
Python Recommender Engine
```

---

## 🧾 Database Design

Core tables include:
- students  
- faculty  
- admins  
- projects  
- groups  

Relational mapping is handled with Hibernate/JPA.

---

## 📊 Excel → MySQL Integration

Using Python automation:
```
Excel (.xlsx) → Pandas DataFrame → SQLAlchemy → MySQL
```

Example code:

```python
df = pd.read_excel("students_data.xlsx")
engine = create_engine("mysql+mysqlconnector://root:pwd@localhost/mentorlink")
df.to_sql("students", con=engine, if_exists="append", index=False)
```

---

## 🔐 Authentication Flow

1. User logs in  
2. Server returns a JWT  
3. JWT required in `Authorization: Bearer <token>` for protected APIs  
4. Token validated via Spring Security filters  

---

## 🧰 Installation & Setup

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/mentorlink-backend.git
cd mentorlink-backend
```

### 2. Configure database
Modify `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mentorlink
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

---
<img width="364" height="269" alt="image" src="https://github.com/user-attachments/assets/cdd77ca7-3909-4beb-8f8a-946277630e7f" />


## 🧪 Testing with Postman

The backend APIs can be tested using an included Postman collection:
- Registration
- Login
- Token usage
- Project creation
- Group join via token
- Faculty assignment

---

## 🧬 Recommender System (Python)

A Flask-based microservice recommends the best faculty for a project using:
- Sentence Transformers
- Cosine Similarity
- NLP embeddings

---

## 🔮 Future Enhancements

- Email notifications  
- Advanced analytics dashboard  
- Microservices with Docker  
- Cloud deployment  

## 🏁 License

MIT License
