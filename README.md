# 🖥️ PC Configurer

Backend application for configuring personal computers.  
Allows managing components (GPU, motherboards, etc.), users, and PC builds.

---

## 🚀 Features

- 📦 Component management:
- 👤 User management
- 🔐 Authentication and authorization (JWT)
- 🧩 Product categories
- 🔎 Fetching and filtering configurations
- 🌐 REST API for frontend

---

## 🏗️ Technologies

- Java 17
- Spring Boot
- Spring Security
- JWT (authentication)
- Hibernate / JPA
- PostgreSQL
- Maven

---

## ⚙️ Configuration

In order to work, you need to use JavaInternetShop backend server made by [@BlackyCGS](https://www.github.com/BlackyCGS)

Settings are located in `application.properties`:

```properties
server.port=8081

spring.datasource.url=${CONFIGURER_POSTGRES_PATH}
spring.datasource.username=${CONFIGURER_POSTGRES_NAME}
spring.datasource.password=${CONFIGURER_POSTGRES_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

security.jwt.secret-key=${JWT_SECRET}
security.jwt.expiration-time=600000
security.jwt.refresh.expiration-time=72000000
```

## .env file
Env file contains:
```
CONFIGURER_POSTGRES_PATH - Database path
CONFIGURER_POSTGRES_NAME - Database name
CONFIGURER_POSTGRES_PASSWORD - Database password
JWT_SECRET - JWT secret
EXTERNAL_API_ADDRESS - internet shop url
EXTERNAL_API_PORT - internet shop backend port
REDIRECT_PORT  - internet shop frontend port
```
