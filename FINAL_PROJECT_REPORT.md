# Final Project Report: TaskForce - A Web-Based Task Management System

---

## 1. Report Header

**Institution**: Faculty of Computer Science and Information Technology, Universiti Putra Malaysia  
**Course**: CSC3402 / CCS3402 - Database Application Development  
**Semester**: Second Semester 2025/2026  
**GitHub Repository**: `https://github.com/pingwenchao/TaskForce-Project.git`  

### Team Members

| Name | Matric No. | Role |
|------|------------|------|
| Ping Wenchao | 226969 | Project Lead |
| Chen Shuaipeng | 228163 | Frontend Lead |
| Pan Ziheng | 226922 | Database Lead |
| Sheng Jiawei | 226994 | Security & QA Lead |

---

## 2. Introduction

In the modern landscape of software development and project management, effective team collaboration is paramount. The success of a project often hinges on the clarity of communication, the transparency of responsibilities, and the ability to track progress in real-time. To address these fundamental needs, our team has designed and developed **TaskForce**, a robust, web-based database application aimed at simplifying and centralizing task management.

TaskForce serves as a single source of truth for project tasks, enabling teams to move away from scattered spreadsheets and disjointed communication channels. The application is built upon a powerful and modern technology stack, with the **Spring Framework** (specifically **Spring Boot 3.x**) at its core. This choice provides a solid foundation for building enterprise-grade applications, emphasizing performance, security, and scalability.

### Primary Project Objectives

The primary objective of this project was to apply database application development principles in a practical setting. This involved:

1. **Designing a normalized relational database schema** targeting **Oracle Database** with proper use of sequences for ID generation
2. **Implementing a full suite of CRUD (Create, Read, Update, Delete) functionalities**
3. **Architecting the application according to the well-established Model-View-Controller (MVC) pattern**
4. **Implementing a role-based access control (RBAC) security model** to ensure data integrity and proper user permissions
5. **Providing multi-language support** (English, Bahasa Melayu, and Chinese) for global accessibility
6. **Maintaining comprehensive audit trails** for all task status changes

This report provides a comprehensive overview of the TaskForce application's design, architecture, features, and the development process.

---

## 3. Database Design

The database architecture is the cornerstone of the TaskForce application, engineered to ensure data integrity, minimize redundancy, and efficiently manage the relationships between core business entities. We employed a relational model, implemented using the **Java Persistence API (JPA)** with **Hibernate** as the persistence provider. This approach allows for a clean mapping between Java objects and database tables.

### 3.1 Database Overview

The application uses **four primary tables** with the `_V6` suffix to ensure uniqueness in the Oracle database environment:

| Table Name | Description |
|------------|-------------|
| `USERS_V6` | User accounts and authentication data with role-based access control |
| `PROJECTS_V6` | Project information and manager assignments |
| `TASKS_V6` | Task details, status, priority, and assignments |
| `TASK_LOGS_V6` | Audit trail of task status changes with timestamps |

### 3.2 Entity-Relationship Diagram (Conceptual)

[**此处插入截图 1**：数据库实体关系图 (ER Diagram)。请使用 Visio, Draw.io 或 IDEA 自带的 Database 工具生成的 ER 图，需清晰展示 USERS_V6, PROJECTS_V6, TASKS_V6, TASK_LOGS_V6 四张表及其主外键关系]

**Entity Relationships Summary**:
- `USERS_V6 (1) ──→ (N) PROJECTS_V6` (as manager)
- `USERS_V6 (1) ──→ (N) TASKS_V6` (as assignee)
- `PROJECTS_V6 (1) ──→ (N) TASKS_V6`
- `TASKS_V6 (1) ──→ (N) TASK_LOGS_V6`
- `USERS_V6 (1) ──→ (N) TASK_LOGS_V6` (as changed_by)

### 3.3 Entity Implementation Details

Below are the detailed implementations of each entity, including the Java code that defines the corresponding database table structure.

#### 3.3.1 User Entity

The User entity represents an actor within the system. Each user has a specific role that dictates their permissions.

```java
package com.upm.taskforce.entity;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a system user for authentication and role-based access.
 * ADMIN users manage projects; EMPLOYEE users are assigned tasks.
 */
@Entity
@Table(name = "USERS_V6")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "USERS_V6_SEQ", allocationSize = 1)
    private Long id;

    /** Unique login identifier */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /** BCrypt-encoded password */
    @Column(nullable = false)
    private String password;

    /** Security role: ADMIN or EMPLOYEE */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /** Projects this user manages (only meaningful for ADMIN) */
    @OneToMany(mappedBy = "manager")
    private Set<Project> managedProjects;

    /** Tasks assigned to this user (typically EMPLOYEE) */
    @OneToMany(mappedBy = "assignee")
    private Set<Task> assignedTasks;

    // Getters and Setters omitted for brevity
}
```

**Description**: 
- `@Entity` marks this class as a JPA entity mapped to `USERS_V6` table
- `@SequenceGenerator` uses Oracle Sequence `USERS_V6_SEQ` for ID generation (critical for Oracle compatibility)
- `@Column(unique = true, nullable = false)` ensures that username must exist and be unique
- `@Enumerated(EnumType.STRING)` stores the role as a string in the database for clarity
- Passwords are encrypted using BCrypt for security

#### 3.3.2 Role Enum

```java
package com.upm.taskforce.entity;

/**
 * Defines user roles for access control.
 * Ensures type-safe role management and avoids "magic strings".
 */
public enum Role {
    ROLE_ADMIN,
    ROLE_EMPLOYEE
}
```

#### 3.3.3 Project Entity

The Project entity allows for the grouping of related tasks, managed by an admin user.

```java
package com.upm.taskforce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a project that contains multiple tasks.
 * Managed by an ADMIN user.
 */
@Entity
@Table(name = "PROJECTS_V6")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projects_seq")
    @SequenceGenerator(name = "projects_seq", sequenceName = "PROJECTS_V6_SEQ", allocationSize = 1)
    private Long id;

    /** Project display name */
    @Column(nullable = false, length = 100)
    private String projectName;

    /** Optional project description */
    @Column(length = 255)
    private String description;

    /** ADMIN user who manages this project */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    /** Tasks belonging to this project */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Task> tasks;

    /** Timestamp of project creation */
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters omitted for brevity
}
```

#### 3.3.4 Task Entity

The Task entity is the central object of the application, representing a single piece of work within a project.

```java
package com.upm.taskforce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a unit of work within a project.
 * Tracks status, priority, and assignment.
 */
@Entity
@Table(name = "TASKS_V6")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_seq")
    @SequenceGenerator(name = "tasks_seq", sequenceName = "TASKS_V6_SEQ", allocationSize = 1)
    private Long id;

    /** Short title summarising the task */
    @Column(nullable = false, length = 100)
    private String title;

    /** Detailed description of the work */
    @Column(length = 200)
    private String description;

    /** Current state: TODO, IN_PROGRESS, DONE */
    @Column(nullable = false, length = 20)
    private String status;

    /** Urgency level: HIGH, MEDIUM, LOW */
    @Column(nullable = false, length = 20)
    private String priority;

    /** Parent project this task belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /** Employee assigned to complete the task (nullable if unassigned) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignee;

    /** Audit trail records for status changes */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changeTime DESC")
    private java.util.List<TaskLog> logs = new java.util.ArrayList<>();

    /** Timestamp of task creation */
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters omitted for brevity
}
```

**Description**: 
- `@ManyToOne` defines relationships creating foreign key columns (`project_id` and `assigned_to_id`)
- `@OneToMany` with `cascade = CascadeType.ALL` ensures that task logs are managed with their parent task
- Status values: `TODO`, `IN_PROGRESS`, `DONE`
- Priority values: `HIGH`, `MEDIUM`, `LOW`

#### 3.3.5 TaskLog Entity

The TaskLog entity provides a crucial audit trail, recording every status change for a task.

```java
package com.upm.taskforce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable record of a task status change.
 * Maintains an audit trail for accountability.
 */
@Entity
@Table(name = "TASK_LOGS_V6")
public class TaskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_logs_seq")
    @SequenceGenerator(name = "task_logs_seq", sequenceName = "TASK_LOGS_V6_SEQ", allocationSize = 1)
    private Long id;

    /** The task whose status changed */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /** User who performed the change */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private User changedBy;

    /** Status before the update (null if this is the initial log entry) */
    @Column(length = 20)
    private String oldStatus;

    /** Status after the update */
    @Column(nullable = false, length = 20)
    private String newStatus;

    /** When the change was recorded (format: yyyy-MM-dd HH:mm:ss) */
    @Column(nullable = false)
    private LocalDateTime changeTime = LocalDateTime.now();

    // Getters and Setters omitted for brevity
}
```

---

## 4. Application Design (MVC Architecture)

The TaskForce application is architected using the **Model-View-Controller (MVC)** pattern, a cornerstone of modern web development that promotes a clean separation of concerns.

[**此处插入截图 2**：IntelliJ IDEA 的项目目录树 (Project Structure)，需展开 controller, service, entity, repository 和 templates 文件夹，展示 MVC 架构层级]

### 4.1 Project Structure Overview

```
TaskForce/
├── src/main/java/com/upm/taskforce/
│   ├── TaskForceApplication.java          # Main application entry point
│   ├── config/                            # Configuration classes
│   │   ├── SecurityConfig.java            # Spring Security configuration
│   │   ├── WebConfig.java                 # i18n and locale configuration
│   │   └── DemoDataInitializer.java       # Demo data initialization
│   ├── controller/                        # Web controllers (MVC: Controller)
│   ├── dto/                               # Data Transfer Objects
│   ├── entity/                            # JPA Entities (MVC: Model)
│   ├── exception/                         # Exception handling
│   ├── repository/                        # Spring Data JPA repositories
│   ├── service/                           # Business logic layer
│   └── util/                              # Utility classes (DatabaseResetter)
└── src/main/resources/
    ├── GroupProject/                      # i18n message bundles
    ├── static/                            # Static resources (CSS, images)
    └── templates/                         # Thymeleaf templates (MVC: View)
```

### 4.2 Model Layer

The Model encapsulates the data and the business rules.

| Component | Responsibility |
|-----------|----------------|
| **Entities** | Define the structure of our data (User, Project, Task, TaskLog) |
| **Repositories** | Spring Data JPA interfaces providing full CRUD operations and custom queries |
| **Services** | Acts as a bridge between Controllers and Repositories, handling business logic and transactions |

**Code Snippet (TaskService.java - Business Logic)**:

```java
package com.upm.taskforce.service;

import com.upm.taskforce.entity.Project;
import com.upm.taskforce.entity.Role;
import com.upm.taskforce.entity.Task;
import com.upm.taskforce.entity.User;
import com.upm.taskforce.entity.TaskLog;
import com.upm.taskforce.repository.TaskLogRepository;
import com.upm.taskforce.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manages task lifecycle operations with transactional integrity.
 * Ensures audit logs are recorded for every status change.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskLogRepository taskLogRepository;

    /**
     * Updates task status and records an audit log entry.
     * The operation is atomic: status update and log persist together.
     * Logs the status change with actor information.
     */
    @Transactional
    public void updateTaskStatus(Long taskId, String newStatus, String actorUsername) {
        User actor = userService.findByUsername(actorUsername);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.warn("Status update attempted on non-existent task: ID={}", taskId);
                    return new IllegalArgumentException("Task not found");
                });

        // Security Check: Only admin or assignee can update status
        boolean isAdmin = actor.getRole() == Role.ROLE_ADMIN;
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(actor.getId());

        if (!isAdmin && !isAssignee) {
            logger.warn("Unauthorized status update attempt: User={} tried to update TaskID={}", actorUsername, taskId);
            throw new SecurityException("User does not have permission to update this task's status.");
        }

        String oldStatus = task.getStatus();
        task.setStatus(newStatus);
        taskRepository.save(task);

        // Create an audit log
        TaskLog log = new TaskLog();
        log.setTask(task);
        log.setChangedBy(actor);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        taskLogRepository.save(log);

        logger.info("Task status updated: ID={}, OldStatus={}, NewStatus={}, ChangedBy={}",
                    taskId, oldStatus, newStatus, actor.getUsername());
    }
}
```

### 4.3 View Layer

The View renders the user interface. We used **Thymeleaf** as our server-side templating engine for dynamic HTML generation. The application supports **three languages** (English, Bahasa Melayu, Chinese) with dynamic switching.

**Code Snippet (dashboard.html - Dynamic UI)**:

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <title th:text="#{app.title}">TaskForce</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div th:replace="~{fragments/navbar :: navbar}"></div>

<div class="container mt-4">
    <div class="card">
        <div class="card-body">
            <table class="table table-borderless">
                <thead>
                <tr>
                    <th th:text="#{col.title}">Title</th>
                    <th th:text="#{col.status}">Status</th>
                    <th th:text="#{col.priority}">Priority</th>
                    <th th:text="#{col.actions}">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr th:each="task : ${taskPage.content}">
                    <td>
                        <a th:href="@{/tasks/{id}(id=${task.id})}" th:text="${task.title}">Title</a>
                    </td>
                    <td>
                        <div class="status-container">
                            <!-- Status badge with color -->
                            <span class="badge status-badge" 
                                  th:classappend="${task.status == 'TODO' ? 'bg-warning text-dark' : 
                                                 (${task.status == 'IN_PROGRESS' ? 'bg-primary' : 'bg-success'})"
                                  th:text="#{'status.' + ${task.status}}">STATUS</span>
                            
                            <form th:action="@{/tasks/{id}/status(id=${task.id})}" method="post" class="status-form">
                                <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
                                <select name="newStatus" class="form-select form-select-sm status-select"
                                        th:disabled="${!#authorization.expression('hasRole(''ADMIN'')') and (task.assignee == null or #authentication.principal.username != task.assignee.username)}">
                                    <option value="" th:text="#{prompt.status_change}">Change...</option>
                                    <option value="TODO" th:text="#{status.TODO}">TODO</option>
                                    <option value="IN_PROGRESS" th:text="#{status.IN_PROGRESS}">IN_PROGRESS</option>
                                    <option value="DONE" th:text="#{status.DONE}">DONE</option>
                                </select>
                                <button type="submit" class="btn btn-sm btn-primary" th:text="#{btn.update}"
                                        th:disabled="${!#authorization.expression('hasRole(''ADMIN'')') and (task.assignee == null or #authentication.principal.username != task.assignee.username)}">Update</button>
                            </form>
                        </div>
                    </td>
                    <td>
                        <!-- Priority badge with color -->
                        <span class="badge priority-badge"
                              th:classappend="${task.priority == 'HIGH' ? 'bg-danger' : 
                                             (${task.priority == 'MEDIUM' ? 'bg-warning text-dark' : 'bg-success'})"
                              th:text="#{'priority.' + ${task.priority}}">PRI</span>
                    </td>
                    <td class="action-buttons">
                        <!-- ADMIN can see enabled buttons -->
                        <div sec:authorize="hasRole('ADMIN')">
                            <a th:href="@{/tasks/{id}/edit(id=${task.id})}" class="btn btn-sm btn-outline-primary me-1" th:text="#{btn.action_edit}">Edit</a>
                            <form th:action="@{/tasks/delete}" method="post" class="d-inline">
                                <input type="hidden" name="id" th:value="${task.id}" />
                                <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
                                <button type="submit" class="btn btn-sm btn-outline-danger" th:text="#{btn.action_delete}">Delete</button>
                            </form>
                        </div>
                        <!-- EMPLOYEE sees disabled grey buttons -->
                        <div sec:authorize="!hasRole('ADMIN')">
                            <a href="#" class="btn btn-sm btn-outline-secondary btn-disabled me-1" th:text="#{btn.action_edit}" onclick="alert(permissionDeniedMsg); return false;">Edit</a>
                            <button class="btn btn-sm btn-outline-secondary btn-disabled" th:text="#{btn.action_delete}" onclick="alert(permissionDeniedMsg); return false;">Delete</button>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
```

### 4.4 Controller Layer

The Controller handles incoming HTTP requests and calls the appropriate service methods.

**Code Snippet (TaskController.java - Request Handling & Security)**:

```java
package com.upm.taskforce.controller;

import com.upm.taskforce.entity.Project;
import com.upm.taskforce.entity.Task;
import com.upm.taskforce.service.ProjectService;
import com.upm.taskforce.service.TaskService;
import com.upm.taskforce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Manages all task-related web requests.
 * Supports create, read, update (including status transitions), and delete operations.
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Deletes a task by ID.
     * Only ADMIN users can delete tasks.
     */
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTask(@RequestParam Long id) {
        taskService.deleteTask(id);
        return "redirect:/dashboard";
    }

    /**
     * Updates the status of a task and records the change in the audit log.
     * The currently authenticated user is recorded as the actor.
     * All authenticated users can update task status (with permission checks).
     */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String newStatus,
                               Authentication authentication) {
        taskService.updateTaskStatus(id, newStatus, authentication.getName());
        return "redirect:/dashboard";
    }
}
```

---

## 5. User Interfaces and Data Persistence Testing (CRUD Validation)

To rigorously demonstrate that our application successfully interacts with the underlying database management system, we conducted end-to-end testing. The following test cases capture the Frontend UI actions alongside the corresponding Backend SQL Database execution results.

### 5.1 Application Entry (Read/Authentication)

Users securely log into the system. The password entered is hashed via BCrypt and compared against the database.

[**此处插入截图 3** - Frontend UI: 登录页面 (login.html) 的截图，显示中英文多语言选项]

[**此处插入截图 4** - Backend DB: 数据库 SELECT * FROM USERS_V6; 的截图，需展示密码已被成功加密 (如 $2a$10$... 格式的密文)，以及角色字段为 ROLE_ADMIN 或 ROLE_EMPLOYEE]

**Demo Credentials for Testing**:
| Username | Password | Role |
|----------|----------|------|
| `admin` | `Password123` | ADMIN |
| `user` | `Password123` | EMPLOYEE |
| `test` | `Password123` | EMPLOYEE |

### 5.2 CREATE Operation: Task Insertion

**Scenario**: An administrator creates a new task assigned to an employee.

- **Frontend Action**: The admin fills out the "New Task" form on the dashboard and clicks Submit.

[**此处插入截图 5** - Frontend UI: 填写"新建任务"表单并点击保存时的截图，显示任务标题、描述、优先级、项目选择、分配用户等字段]

- **Database Verification**: A query is executed in the database to prove the record was successfully inserted.

[**此处插入截图 6** - Backend DB: 数据库 SELECT * FROM TASKS_V6 WHERE title = 'xxx'; 的截图，展示刚刚新建的任务已写入数据库，包含完整的字段值和时间戳格式 yyyy-MM-dd HH:mm:ss]

### 5.3 CREATE Operation: Project Insertion

**Scenario**: An administrator creates a new project.

[**截图可选项**：展示 Project 新建功能]

### 5.4 UPDATE Operation & Audit Logging

**Scenario**: A user updates the status of their assigned task from TODO to DONE.

- **Frontend Action**: The user selects the new status from the dropdown menu and clicks Update button. The UI updates immediately.

[**此处插入截图 7** - Frontend UI: Dashboard 界面，展示某条任务的状态已变更为 DONE，显示绿色的状态标签]

- **Database Verification**: The system utilizes `@Transactional` to update the TASKS_V6 table and simultaneously insert a new record into the TASK_LOGS_V6 table.

[**此处插入截图 8** - Backend DB: 数据库 SELECT * FROM TASK_LOGS_V6; 的截图，必须清晰展示 OLD_STATUS 为 TODO, NEW_STATUS 为 DONE，以及 CHANGE_TIME 时间戳，证明数据库触发了更新和日志写入]

### 5.5 UPDATE Operation: Task Details Edit

**Scenario**: An admin edits task details (title, description, priority, assignment).

[**截图可选项**：展示 Task 编辑表单]

### 5.6 READ Operation: Dashboard View & Pagination

**Scenario**: User views the dashboard with paginated task list.

[**截图可选项**：展示 Dashboard 界面和分页控件]

### 5.7 READ Operation: Task Detail & Audit Log

**Scenario**: User views a single task's complete details and audit history.

[**此处插入截图（可选）** - Frontend UI: Task Detail 页面，展示任务信息和完整的状态变更历史记录表格]

### 5.8 DELETE Operation & Security Enforcement

**Scenario**: A task is permanently removed from the system.

- **Frontend Action**: An Admin clicks the red "DELETE" button.

[**此处插入截图 9** - Frontend UI: Admin 点击删除前后的对比，或者删除成功后返回 Dashboard 的提示]

- **Database Verification**: The record is entirely expunged from persistent storage.

[**此处插入截图 10** - Backend DB: 数据库 SELECT * FROM TASKS_V6 WHERE id = xxx; 的截图，展示查询结果为空 (0 rows selected)，证明数据已被物理删除]

- **RBAC Enforcement**: When a standard EMPLOYEE attempts to access the delete endpoint directly, the system intercepts the request.

[**此处插入截图 11** - Frontend UI: 普通用户尝试执行删除或访问管理员页面时，系统拦截并弹出的 403 Forbidden 错误页面，或禁用按钮的灰色显示]

### 5.9 Project Management (Admin Only)

**Scenario**: Admin manages projects (create, view, delete).

[**截图可选项**：展示 Project List 页面]

### 5.10 Multi-Language Support (i18n)

**Scenario**: User switches language between English, Bahasa Melayu, and Chinese.

---

## 6. Evidence of Group Meetings and Tasks Allocation

Effective teamwork and version control were essential to the success of this project. We utilized GitHub for code collaboration.

### 6.1 GitHub Repository & Collaboration Evidence

The complete source code and commit history are publicly available at:

`https://github.com/pingwenchao/TaskForce-Project.git`

[**此处插入截图 12**：GitHub 仓库的 Commit History (提交记录) 或 Insights -> Contributors 的截图，必须能看到 Ping Wenchao, Chen Shuaipeng 等不同组员的代码提交记录，证明团队合作]

[**此处插入截图 13**：小组开会、任务分配的证明截图 (如微信群聊探讨技术问题、Teams/Zoom 视频会议截图、或任务看板)]

### 6.2 Task Allocation

| Team Member | Matric No. | Primary Responsibilities & Contributions |
|-------------|------------|-------------------------------------------|
| **Ping Wenchao** | 226969 | **Project Lead**, Backend Architecture (Service Layer, Transaction Management, Security Configuration, User Authentication, DatabaseResetter utility). Core contributor to TaskService, SecurityConfig, and DemoDataInitializer. |
| **Pan Ziheng** | 226922 | **Database Lead** (JPA Entities, Repository Interfaces, Data Initialization, Oracle Sequence configuration, ER diagram design). Core contributor to entity classes and repository layer. |
| **Chen Shuaipeng** | 228163 | **Frontend Lead** (Thymeleaf Templates, Bootstrap CSS, UI/UX Design, multi-language support implementation, dashboard layout with fixed status columns). Core contributor to all HTML templates. |
| **Sheng Jiawei** | 226994 | **Security & QA Lead** (Controller-level security testing, Role-based access control, Version Control management, README documentation, final report). Core contributor to GlobalExceptionHandler and testing validation. |

---

## 7. Technical Stack Summary

### Backend Technologies

- **Java 17** - Modern Java with LTS support
- **Spring Boot 3.x** - Application framework
- **Spring Security** - Authentication and authorization with BCrypt password encoding
- **Spring Data JPA** - ORM and data access with Hibernate
- **Thymeleaf** - Server-side template engine
- **HikariCP** - High-performance JDBC connection pool for Oracle
- **Maven** - Build and dependency management

### Frontend Technologies

- **HTML5** - Semantic markup
- **Bootstrap 5** - CSS framework for responsive design
- **CSS3** - Custom styling
- **JavaScript** - Client-side interactivity

### Database Technologies

- **Oracle Database** - Relational DBMS (UPM campus)
- **Oracle Sequences** - ID generation (not identity columns)
- **JPA/Hibernate** - ORM mapping

### Development Tools

- **IntelliJ IDEA** - Primary IDE
- **Git & GitHub** - Version control and collaboration
- **Oracle SQL Developer** - Database management and testing

---

## 8. Conclusion

The development of the TaskForce application has been a highly successful endeavor, culminating in a fully functional, secure, and well-architected web application that meets all core project requirements. Through this project, our team has demonstrated a comprehensive understanding of database application development using the Spring Framework.

### Key Achievements

1. **Normalized Database Schema**: Successfully implemented four relational tables with proper foreign key constraints using Oracle Sequences for ID generation
2. **Full CRUD Operations**: Complete Create, Read, Update, Delete functionality for tasks, projects, and users
3. **Role-Based Access Control**: Sophisticated RBAC system with ADMIN and EMPLOYEE roles
4. **Audit Trail & Logging**: Complete task status change history with timestamps and user attribution
5. **MVC Architecture**: Clean separation of concerns following Spring MVC best practices
6. **Multi-language Support**: English, Bahasa Melayu, and Chinese with dynamic switching
7. **Transaction Management**: Proper use of `@Transactional` for data integrity
8. **Security**: BCrypt password hashing, CSRF protection, and method-level security
9. **Responsive UI**: Bootstrap-based interface with fixed columns to prevent layout shifting
10. **Time Formatting**: Consistent `yyyy-MM-dd HH:mm:ss` display format

### Validation of Persistent Storage

By validating UI interactions directly against Backend SQL queries, we have proven the reliability of our persistent storage mechanisms. The adherence to the MVC design pattern has resulted in a codebase that is modular, maintainable, and scalable.

### Teamwork & Version Control

The use of tools like Git and GitHub facilitated effective collaboration and version control, which were integral to our workflow. All team members contributed actively to the codebase, with clear division of responsibilities.

### Future Enhancements

While the current version of TaskForce is a complete product in its own right, there are several avenues for future enhancement:

- Implementation of a RESTful API for mobile client consumption
- Real-time features like WebSocket notifications for task updates
- File attachments for tasks
- Advanced reporting and analytics
- Email notifications for task assignments

### Final Remarks

In conclusion, this project has provided our team with invaluable hands-on experience in building real-world software and has solidified our understanding of the principles and practices of modern enterprise application development. The TaskForce application successfully fulfills all requirements of the CSC3402 / CCS3402 course, including:

✅ Web-based database application design and development using Spring framework  
✅ Teamwork and collaboration with proper task allocation  
✅ Proficiency in development tools (IntelliJ), version control (Git), and collaboration (GitHub)

---

**Built with ❤️ for UPM CSC3402 / CCS3402**
