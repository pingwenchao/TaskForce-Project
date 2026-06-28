# TaskForce - Enterprise Task Tracking System

This is an enterprise-grade task management and tracking system built with Spring Boot and Thymeleaf. The project aims to provide a secure, multilingual, and user-friendly platform for project and task management.

## ✨ Core Features

- **User Authentication & Authorization**:
  - Secure registration and login functionality.
  - Password encryption and session management using Spring Security.
- **Role-Based Access Control**:
  - **ADMIN**: Can access all pages, including project creation and management.
  - **USER**: Can only access their dashboard and assigned tasks; no access to project management.
- **Internationalization (i18n)**:
  - Supports three languages: English (EN), Malay (MS), and Chinese (ZH).
  - Users can switch languages on the fly from the UI.
- **Responsive User Interface**:
  - Built with Bootstrap 5 for a great experience on both desktop and mobile devices.
  - Consistent look and feel with a unified navigation bar and page layout.
- **Automated Database Initialization**:
  - The database is automatically cleared and seeded with demo data on startup.
  - Includes pre-defined users (`admin`/`password` and `user`/`password`), projects, and tasks.
- **Custom Error Handling**:
  - Provides styled, user-friendly pages for HTTP errors like 403 (Forbidden).

## 🛠️ Tech Stack

- **Backend**:
  - [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  - [Spring Boot](https://spring.io/projects/spring-boot)
  - [Spring Security](https://spring.io/projects/spring-security) (for authentication & authorization)
  - [Spring Data JPA](https://spring.io/projects/spring-data-jpa) (for data persistence)
  - [Thymeleaf](https://www.thymeleaf.org/) (server-side template engine)
  - [Maven](https://maven.apache.org/) (build and dependency management)
- **Frontend**:
  - HTML5
  - [Bootstrap 5](https://getbootstrap.com/)
  - [Bootstrap Icons](https://icons.getbootstrap.com/)
  - CSS3
- **Database**:
  - H2 (in-memory database for development and demo purposes)

## 🚀 How to Run

1.  **Clone the repository**:
    ```bash
    git clone <your-repository-url>
    ```

2.  **Navigate to the project directory**:
    ```bash
    cd TaskForce
    ```

3.  **Run the application using Maven**:
    ```bash
    mvn spring-boot:run
    ```
    The application will start, and the database will be automatically initialized with demo data.

4.  **Access the application**:
    Open your browser and go to `http://localhost:8080`.

    You can log in with the following credentials:
    - **Admin**: `admin` / `password`
    - **User**: `user` / `password`

## 📁 Project Structure

```
TaskForce/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/com/upm/taskforce/   # Java source code
│   │   │   ├── config/               # Spring Security & Web configuration
│   │   │   ├── controller/           # Controllers (handle HTTP requests)
│   │   │   ├── entity/               # JPA entities
│   │   │   ├── repository/           # Data repositories
│   │   │   └── service/              # Business logic services
│   │   └── resources/
│   │       ├── static/               # Static assets (CSS, JS, images)
│   │       ├── templates/            # Thymeleaf templates
│   │       ├── schema.sql            # Drops database tables on startup
│   │       ├── data.sql              # Seeds database with demo data
│   │       └── application.properties # Spring Boot configuration
│   └── test/
├── .gitignore                        # Git ignore file
├── pom.xml                           # Maven Project Object Model
└── README.md                         # This documentation
```
