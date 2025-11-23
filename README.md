# 🛒 E-Commerce Backend API (Spring Boot)

A robust, feature-rich RESTful API for an E-commerce platform built using **Java** and **Spring Boot**. This backend handles everything from user authentication and product management to shopping carts and order processing.

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)
![JWT](https://img.shields.io/badge/Security-JWT-red)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20UI-85ea2d)

## 🌟 Key Features

* **User Management**: Secure User Registration (Signup), Login (Signin), and Role-based access (Admin/User).
* **Security**: Stateless authentication using **JSON Web Tokens (JWT)**.
* **Product Catalog**: 
    * Create, Update, Delete products (Admin).
    * Browse products with pagination and sorting.
    * Search products by keyword.
    * Filter products by Category.
* **Category Management**: Organize products into hierarchical categories.
* **Shopping Cart**: 
    * Add items to cart.
    * Update item quantities.
    * Remove items.
    * Auto-calculation of total prices.
* **Address Management**: Users can manage multiple shipping addresses.
* **Order Processing**: 
    * Place orders with different payment methods.
    * Automatic stock reduction upon order placement.
    * Order history tracking.
* **API Documentation**: Integrated Swagger UI for testing endpoints.

## 🛠️ Tech Stack

* **Core Framework**: Spring Boot 3+
* **Language**: Java 17
* **Database**: PostgreSQL
* **ORM**: Hibernate / Spring Data JPA
* **Security**: Spring Security, JWT (JJWT)
* **Documentation**: SpringDoc OpenAPI (Swagger UI)
* **Tools**: Maven, Lombok, ModelMapper

## 📂 Project Structure

The project follows a standard layered architecture:
* `Controller`: Handles HTTP requests and API endpoints.
* `Service`: Business logic and transaction management.
* `Repository`: Database interactions using JPA.
* `Model`: JPA Entities mapping to database tables.
* `Payload`: DTOs (Data Transfer Objects) for requests and responses.
* `Security`: JWT configuration and filters.
* `Config`: App configurations (Swagger, Constants).

## 📊 Database Design (ER Diagram)

A detailed Entity-Relationship (ER) diagram is available in the project root:
📄 **[ecommerce-er-diagram.pdf](./ecommerce-er-diagram.pdf)**

## 🚀 Getting Started

### Prerequisites
* Java Development Kit (JDK) 17 or higher
* Maven
* PostgreSQL installed and running

### Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/mayank1008-tech/ecommerce-backend-spring-boot.git](https://github.com/mayank1008-tech/ecommerce-backend-spring-boot.git)
    cd ecommerce-backend-spring-boot
    ```

2.  **Configure Database**
    * Create a PostgreSQL database named `ecommerce_db` (or whatever you prefer).
    * Open `src/main/resources/application.properties` and update your DB credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
    spring.datasource.username=your_postgres_username
    spring.datasource.password=your_postgres_password
    ```

3.  **Run the Application**
    ```bash
    mvn spring-boot:run
    ```

The server will start on `http://localhost:8080`.

## 📖 API Documentation

Once the server is running, you can access the interactive Swagger UI documentation at:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Main Endpoints Overview

| Module | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/signin` | Login user & get Token |
| | `POST` | `/api/auth/signup` | Register new user |
| **Products** | `GET` | `/api/public/products` | Get all products |
| | `POST` | `/api/admin/categories/{catId}/product` | Add product (Admin) |
| **Cart** | `POST` | `/api/carts/products/{id}/quantity/{qty}` | Add Item to Cart |
| | `GET` | `/api/carts/users/cart` | Get User's Cart |
| **Orders** | `POST` | `/api/order/users/payments/{method}` | Place an Order |

*(Check Swagger UI for the complete list of all endpoints and parameters)*

## 🤝 Contributing

Contributions are welcome!
1.  **Fork** the project.
2.  Create your feature **Branch** (`git checkout -b feature/AmazingFeature`).
3.  **Commit** your changes (`git commit -m 'Add some AmazingFeature'`).
4.  **Push** to the Branch (`git push origin feature/AmazingFeature`).
5.  Open a **Pull Request**.

## 👤 Author

**Mayank Jain**
* GitHub: [@mayank1008-tech](https://github.com/mayank1008-tech)
* LinkedIn: [Mayank Jain](https://www.linkedin.com/in/mayank-jain-78a6bb321/)
---
*This project was built for educational purposes to demonstrate a Spring-Boot e-commerce monolithic architecture.*
