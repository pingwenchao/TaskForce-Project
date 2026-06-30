# TaskForce - Enterprise Task Management System

[![Java Version](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

A modern, secure, and multilingual enterprise-grade task management system built with Spring Boot, designed for the CSC3402/CCS3402 Database Application Development course at Universiti Putra Malaysia (UPM).

---

## Table of Contents

- [About the Project](#about-the-project)
- [Core Features](#core-features)
- [Tech Stack](#tech-stack)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [Demo Credentials](#demo-credentials)
- [Project Structure](#project-structure)
- [Course Requirements Checklist](#course-requirements-checklist)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

---

## About the Project

TaskForce is a comprehensive task management solution that enables teams to organize projects, assign tasks, and track progress efficiently. The system implements role-based access control, full audit logging, and multilingual support, making it suitable for enterprise environments.

### Key Objectives

- Provide a user-friendly interface for task and project management
- Ensure secure authentication and authorization using Spring Security
- Maintain a complete audit trail of all task status changes
- Support multiple languages for global accessibility
- Implement robust CRUD operations with Oracle Database

---

## Core Features

### 🔐 Authentication & Authorization
- **Secure User Registration**: With password strength validation (min 8 chars, uppercase, lowercase, numbers)
- **BCrypt Password Encryption**: Industry-standard password hashing
- **Session Management**: Automatic session fixation protection and cookie management
- **Role-Based Access Control (RBAC)**:
  - **ADMIN**: Full system access, project management, task creation/editing/deletion
  - **EMPLOYEE**: Dashboard access, task viewing, status updates only
- **Visual Permission Indicators**: Disabled buttons appear grayed out with internationalized prompts

### 🌍 Internationalization (i18n)
- **Three Language Support**: English (EN), Bahasa Melayu (MS), 中文 (ZH)
- **Dynamic Language Switching**: Users can change language without re-authentication
- **Consistent Message Bundles**: All UI elements properly localized

### 📊 Project & Task Management
- **Project Creation & Management**: Create, view, edit, and delete projects
- **Task Assignment**: Assign tasks to team members
- **Status Tracking**: 
  - `TODO` - Yellow
  - `IN_PROGRESS` - Blue  
  - `DONE` - Green
- **Priority System**:
  - `HIGH` - Red
  - `MEDIUM` - Yellow
  - `LOW` - Green
- **Task Logs**: Complete audit trail of all status changes with timestamps and user attribution

### 🎨 User Interface
- **Responsive Design**: Built with Bootstrap 5, works seamlessly on desktop and mobile
- **Stable Layout**: Fixed-width columns prevent layout shifting when content changes
- **Navigation Bar**: Unified header with language switcher and user info
- **Custom Error Pages**: Styled 403 Forbidden page with helpful guidance
- **Time Formatting**: Clean `yyyy-MM-dd HH:mm:ss` display format

### 🗄️ Database Integration
- **Oracle Database**: UPM campus database connection
- **JPA & Hibernate**: ORM for seamless database interactions
- **Sequence Generation**: Proper ID management with Oracle sequences
- **Automatic Schema Management**: JPA handles table creation and updates

---

## Tech Stack

### Backend
- **Java 17**: Modern Java with LTS support
- **Spring Boot 3.x**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: ORM and data access
- **Thymeleaf**: Server-side template engine
- **HikariCP**: High-performance JDBC connection pool
- **Maven**: Build and dependency management

### Frontend
- **HTML5**: Semantic markup
- **Bootstrap 5**: CSS framework
- **Bootstrap Icons**: Icon library
- **CSS3**: Custom styling

### Database
- **Oracle Database**: Relational DBMS (UPM campus)

---

## Database Schema

The application uses four main tables with `_V6` suffix:

| Table Name | Description |
|------------|-------------|
| `USERS_V6` | User accounts and authentication data |
| `PROJECTS_V6` | Project information and manager assignments |
| `TASKS_V6` | Task details, status, priority, and assignments |
| `TASK_LOGS_V6` | Audit trail of task status changes |

### Entity Relationships
```
USERS_V6 (1) ──→ (N) PROJECTS_V6 (as manager)
USERS_V6 (1) ──→ (N) TASKS_V6 (as assignee)
PROJECTS_V6 (1) ──→ (N) TASKS_V6
TASKS_V6 (1) ──→ (N) TASK_LOGS_V6
USERS_V6 (1) ──→ (N) TASK_LOGS_V6 (as changed_by)
```

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+ or use the included Maven Wrapper
- Access to UPM Oracle Database or local Oracle installation
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd TaskForce
   ```

2. **Configure Database Connection**

   Edit `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:oracle:thin:@fsktmdbora.upm.edu.my:1521:fsktm
   spring.datasource.username=your_student_id
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

   # JPA Configuration
   spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

3. **Reset Database (Optional but Recommended)**

   If you need a fresh start, execute these statements in Oracle SQL Developer or SQL*Plus:
   ```sql
   -- Drop tables in correct order (due to foreign key constraints)
   DROP TABLE TASK_LOGS_V6 CASCADE CONSTRAINTS;
   DROP TABLE TASKS_V6 CASCADE CONSTRAINTS;
   DROP TABLE PROJECTS_V6 CASCADE CONSTRAINTS;
   DROP TABLE USERS_V6 CASCADE CONSTRAINTS;
   
   -- Drop sequences
   DROP SEQUENCE TASK_LOGS_V6_SEQ;
   DROP SEQUENCE TASKS_V6_SEQ;
   DROP SEQUENCE PROJECTS_V6_SEQ;
   DROP SEQUENCE USERS_V6_SEQ;
   ```

   JPA will automatically recreate the tables and sequences on the next application startup.

4. **Build and Run the Application**

   **Windows:**
   ```bash
   mvnw.cmd clean install
   mvnw.cmd spring-boot:run
   ```

   **Linux/Mac:**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

   Or run directly from your IDE by running `TaskForceApplication.java`.

5. **Access the Application**

   Open your browser and navigate to: `http://localhost:8080`

---

## Demo Credentials

The system automatically initializes demo data when the database is empty:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `password` | ADMIN | Full system access |
| `user` | `password` | EMPLOYEE | Regular employee account |
| `test` | `password` | EMPLOYEE | Secondary employee account |

### Demo Data Includes
- 2 Projects: Project Alpha, Project Beta
- 4 Tasks with various statuses and priorities
- Sample task logs showing status transitions

---

## Project Structure

```
TaskForce/
├── .vscode/
│   ├── launch.json                          # VS Code debug configuration
│   └── settings.json                        # VS Code workspace settings
│
├── src/
│   ├── main/
│   │   ├── java/com/upm/taskforce/
│   │   │   ├── TaskForceApplication.java    # Main application entry point
│   │   │   │
│   │   │   ├── config/                      # Configuration classes
│   │   │   │   ├── DemoDataInitializer.java # Demo data initialization
│   │   │   │   ├── SecurityConfig.java      # Spring Security configuration
│   │   │   │   └── WebConfig.java           # Web MVC & i18n configuration
│   │   │   │
│   │   │   ├── controller/                  # Web controllers
│   │   │   │   ├── DashboardController.java # Dashboard page
│   │   │   │   ├── ErrorController.java     # Custom error handling
│   │   │   │   ├── LoginController.java     # Login & registration
│   │   │   │   ├── ProjectController.java   # Project CRUD operations
│   │   │   │   └── TaskController.java      # Task CRUD & status updates
│   │   │   │
│   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   │   └── UserRegistrationDto.java # User registration form
│   │   │   │
│   │   │   ├── entity/                      # JPA Entities
│   │   │   │   ├── Project.java             # Project entity
│   │   │   │   ├── Role.java                # User role enum (ADMIN, EMPLOYEE)
│   │   │   │   ├── Task.java                # Task entity
│   │   │   │   ├── TaskLog.java             # Task status change log
│   │   │   │   └── User.java                # User entity
│   │   │   │
│   │   │   ├── exception/                   # Exception handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── repository/                  # Spring Data JPA repositories
│   │   │   │   ├── ProjectRepository.java
│   │   │   │   ├── TaskLogRepository.java
│   │   │   │   ├── TaskRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   │
│   │   │   └── service/                     # Business logic layer
│   │   │       ├── ProjectService.java
│   │   │       ├── TaskService.java
│   │   │       ├── UserDetailsServiceImpl.java
│   │   │       ├── UserService.java
│   │   │       └── UsernameAlreadyExistsException.java
│   │   │
│   │   └── resources/
│   │       ├── GroupProject/                # i18n message bundles
│   │       │   ├── Messages.properties      # Default (Chinese)
│   │       │   ├── Messages_en.properties   # English
│   │       │   ├── Messages_ms.properties   # Malay
│   │       │   └── Messages_zh.properties   # Chinese
│   │       │
│   │       ├── static/                      # Static resources
│   │       │   ├── css/
│   │       │   │   └── custom.css           # Custom styling
│   │       │   └── images/
│   │       │       └── logo.png             # Application logo
│   │       │
│   │       ├── templates/                   # Thymeleaf templates
│   │       │   ├── error/
│   │       │   │   └── 403.html             # Access denied page
│   │       │   ├── fragments/
│   │       │   │   ├── language-switcher.html # Language switch component
│   │       │   │   └── navbar.html          # Navigation bar
│   │       │   ├── dashboard.html           # Main dashboard
│   │       │   ├── login.html               # Login page
│   │       │   ├── register.html            # Registration page
│   │       │   ├── project-list.html        # Project listing
│   │       │   ├── project-form.html        # Project create/edit
│   │       │   ├── task-detail.html         # Task details & logs
│   │       │   ├── task-form.html           # Task create
│   │       │   └── task-edit-form.html      # Task edit
│   │       │
│   │       ├── application.properties       # Main configuration
│   │       ├── application-prod.properties  # Production profile
│   │       └── logback-spring.xml           # Logging configuration
│   │
│   └── test/
│       └── java/com/upm/taskforce/
│           └── TaskForceApplicationTests.java # Unit & integration tests
│
├── .gitattributes
├── .gitignore
├── mvnw                                    # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                # Maven Wrapper (Windows)
├── pom.xml                                 # Maven POM file
└── README.md                               # This file
```

---

## Course Requirements Checklist

This project satisfies **all** requirements for CSC3402/CCS3402 Database Application Development:

| Requirement | Status |
|-------------|--------|
| Uses Spring Framework | ✅ Completed |
| Connects to Oracle Database Management System | ✅ Completed |
| Implements full CRUD (Create, Read, Update, Delete) operations | ✅ Completed |
| Follows MVC (Model-View-Controller) architecture | ✅ Completed |
| Includes 4+ user interface pages | ✅ Completed (Dashboard, Login, Register, Projects, Tasks, etc.) |
| Includes 4+ database tables | ✅ Completed (USERS_V6, PROJECTS_V6, TASKS_V6, TASK_LOGS_V6) |

---

## Screenshots

*Add screenshots here once application is running*

1. Dashboard View
2. Project Management
3. Task Details
4. Login Page

---

## Contributing

This is a course project. Contributions are welcome from team members.

1. Create a feature branch (`git checkout -b feature/AmazingFeature`)
2. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
3. Push to the branch (`git push origin feature/AmazingFeature`)
4. Open a Pull Request

---

## License

Distributed under the MIT License. See `LICENSE` file for more information.

---

## Acknowledgments

- Universiti Putra Malaysia (UPM)
- CSC3402/CCS3402 Course Team
- Spring Boot Community
- Bootstrap Community

---

**Built with ❤️ for UPM CSC3402/CCS3402**
