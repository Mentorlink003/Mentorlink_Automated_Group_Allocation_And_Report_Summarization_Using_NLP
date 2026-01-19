
# 🧠 MentorLink Backend
### 🚀 Intelligent Project Mentorship Management System

The **MentorLink Backend** is a secure, scalable, and intelligent backend system built using **Spring Boot**, **MySQL**, and **JWT authentication**, designed to manage the complete lifecycle of student–faculty project mentorship in academic institutions.

It automates everything from **user onboarding and authentication** to **project creation, group formation, faculty assignment**, and **AI-powered recommendations**.

---

## 📌 Why MentorLink?

🎓 Manual project mentoring systems are:
- Time-consuming  
- Error-prone  
- Non-transparent  

💡 **MentorLink solves this** by providing:
- Role-based secure APIs  
- Automated workflows  
- Intelligent NLP-driven recommendations  

---

## 📚 Table of Contents

- 🧠 Overview  
- ⚙️ Key Features  
- 🛠 Tech Stack  
- 🧩 System Architecture  
- 🧾 API Modules  
- 🗄 Database Design  
- 📊 Excel → MySQL Integration  
- 🔐 Authentication Flow  
- 🧰 Installation & Setup  
- 🧪 Testing with Postman  
- 🧬 Recommender System  
- 🔮 Future Enhancements  

---

## 🧠 Overview

MentorLink is built to simplify and digitize the academic project mentoring ecosystem.

### 👥 User Roles
- **Students** → Form groups, submit projects  
- **Faculty** → Mentor and evaluate projects  
- **Admins** → Manage users, projects, and assignments  

Secure access is enforced using **JWT-based authentication**, while **MySQL** ensures reliable and persistent data storage.

---

## ⚙️ Key Features

- Role-based User Management (Student / Faculty / Admin)  
- Secure JWT Authentication & Authorization  
- Project Creation & Lifecycle Management  
- Group Formation using Join Tokens  
- Faculty Assignment & Mentorship  
- AI-based Faculty Recommendation (NLP)  
- Excel → MySQL Bulk Data Upload  
- Clean & RESTful APIs  

---

## 🛠 Tech Stack

| Layer | Technology |
|-----|-----------|
| Backend | Spring Boot (Java 17) |
| Security | Spring Security + JWT |
| Database | MySQL |
| ORM | Hibernate / JPA |
| Recommender | Python (Sentence Transformers) |
| Data Handling | Pandas + SQLAlchemy |
| API Testing | Postman |

---

## 🧩 System Architecture

```
Frontend (Flutter / React)
          │
          ▼
   Spring Boot Backend
          │
          ▼
        MySQL Database
          │
          ▼
 Python NLP Recommender (Flask)
```

---

## 🧾 API Modules

### 🔐 Authentication Module
- Student / Faculty / Admin Registration
- Login & JWT Generation

### 📁 Project Module
- Create Projects
- View & Manage Projects

### 👥 Group Module
- Create Groups
- Join Groups via Token
- Manage Group Members

### 🎓 Faculty Module
- Faculty Allocation
- Mentor Assignment

### 🤖 Recommender Module
- NLP-based Faculty Recommendation
- Skill & Domain Matching

---

## 🗄 Database Design

Core tables:
- students
- faculty
- admins
- projects
- groups

Relational mapping handled via Hibernate/JPA.

---

## 📊 Excel → MySQL Integration

```
Excel (.xlsx)
   ↓
Pandas DataFrame
   ↓
SQLAlchemy
   ↓
MySQL
```

### Example Code
```python
df = pd.read_excel("students_data.xlsx")
engine = create_engine("mysql+mysqlconnector://root:pwd@localhost/mentorlink")
df.to_sql("students", con=engine, if_exists="append", index=False)
```

---

## 🔐 Authentication Flow

1. User logs in  
2. Server generates JWT  
3. Client sends token in header  
   Authorization: Bearer <token>  
4. Spring Security validates token  

---

## 🧰 Installation & Setup

### Clone Repository
```bash
git clone https://github.com/yourusername/mentorlink-backend.git
cd mentorlink-backend
```

### Configure Database
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mentorlink
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Testing with Postman

- Registration APIs  
- Login & JWT  
- Token Authorization  
- Project Creation  
- Group Join via Token  
- Faculty Assignment  

---

## 🧬 Recommender System

A Flask-based microservice using:
- Sentence Transformers  
- NLP Embeddings  
- Cosine Similarity  

Used to match project requirements with faculty expertise.

---

## 🔮 Future Enhancements

- Email & Notification System  
- Analytics Dashboard  
- Dockerized Microservices  
- Cloud Deployment  

---

## 🏁 License

MIT License
