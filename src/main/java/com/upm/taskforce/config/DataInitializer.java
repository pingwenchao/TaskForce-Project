package com.upm.taskforce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // Drop existing objects in reverse order of creation to respect foreign keys
        String[] dropStatements = {
            "DROP TABLE TASK_LOGS_V6 CASCADE CONSTRAINTS",
            "DROP TABLE TASKS_V6 CASCADE CONSTRAINTS",
            "DROP TABLE PROJECTS_V6 CASCADE CONSTRAINTS",
            "DROP TABLE USERS_V6 CASCADE CONSTRAINTS",
            "DROP SEQUENCE TASK_LOGS_V6_SEQ",
            "DROP SEQUENCE TASKS_V6_SEQ",
            "DROP SEQUENCE PROJECTS_V6_SEQ",
            "DROP SEQUENCE USERS_V6_SEQ"
        };

        for (String statement : dropStatements) {
            try {
                jdbcTemplate.execute(statement);
            } catch (Exception e) {
                // Ignore errors if the object doesn't exist
            }
        }

        // Create sequences
        jdbcTemplate.execute("CREATE SEQUENCE USERS_V6_SEQ START WITH 1 INCREMENT BY 1");
        jdbcTemplate.execute("CREATE SEQUENCE PROJECTS_V6_SEQ START WITH 100 INCREMENT BY 1");
        jdbcTemplate.execute("CREATE SEQUENCE TASKS_V6_SEQ START WITH 1000 INCREMENT BY 1");
        jdbcTemplate.execute("CREATE SEQUENCE TASK_LOGS_V6_SEQ START WITH 2000 INCREMENT BY 1");

        // Create tables
        jdbcTemplate.execute(
            "CREATE TABLE USERS_V6 (" +
            "    id NUMBER PRIMARY KEY," +
            "    username VARCHAR2(50) NOT NULL UNIQUE," +
            "    password VARCHAR2(100) NOT NULL," +
            "    role VARCHAR2(20) NOT NULL" +
            ")"
        );

        jdbcTemplate.execute(
            "CREATE TABLE PROJECTS_V6 (" +
            "    id NUMBER PRIMARY KEY," +
            "    project_name VARCHAR2(100) NOT NULL," +
            "    description VARCHAR2(255)," +
            "    manager_id NUMBER NOT NULL," +
            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    CONSTRAINT fk_manager_v6 FOREIGN KEY (manager_id) REFERENCES USERS_V6(id)" +
            ")"
        );

        jdbcTemplate.execute(
            "CREATE TABLE TASKS_V6 (" +
            "    id NUMBER PRIMARY KEY," +
            "    title VARCHAR2(100) NOT NULL," +
            "    description VARCHAR2(1000)," +
            "    status VARCHAR2(20) NOT NULL," +
            "    priority VARCHAR2(20) NOT NULL," +
            "    project_id NUMBER NOT NULL," +
            "    assigned_to_id NUMBER," +
            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    CONSTRAINT fk_project_v6 FOREIGN KEY (project_id) REFERENCES PROJECTS_V6(id)," +
            "    CONSTRAINT fk_assigned_to_v6 FOREIGN KEY (assigned_to_id) REFERENCES USERS_V6(id)" +
            ")"
        );

        jdbcTemplate.execute(
            "CREATE TABLE TASK_LOGS_V6 (" +
            "    id NUMBER PRIMARY KEY," +
            "    old_status VARCHAR2(20)," +
            "    new_status VARCHAR2(20)," +
            "    changed_by_id NUMBER," +
            "    task_id NUMBER," +
            "    change_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    CONSTRAINT fk_changed_by_log_v6 FOREIGN KEY (changed_by_id) REFERENCES USERS_V6(id)," +
            "    CONSTRAINT fk_task_log_v6 FOREIGN KEY (task_id) REFERENCES TASKS_V6(id)" +
            ")"
        );

        // Insert test data
        String adminPassword = "'$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'";
        String userPassword = "'$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'";

        jdbcTemplate.execute("INSERT INTO USERS_V6 (id, username, password, role) VALUES (1, 'admin', " + adminPassword + ", 'ROLE_ADMIN')");
        jdbcTemplate.execute("INSERT INTO USERS_V6 (id, username, password, role) VALUES (2, 'user', " + userPassword + ", 'ROLE_EMPLOYEE')");
        jdbcTemplate.execute("INSERT INTO USERS_V6 (id, username, password, role) VALUES (3, 'test', " + userPassword + ", 'ROLE_EMPLOYEE')");

        jdbcTemplate.execute("INSERT INTO PROJECTS_V6 (id, project_name, description, manager_id) VALUES (101, 'Project Alpha', 'First project', 1)");
        jdbcTemplate.execute("INSERT INTO PROJECTS_V6 (id, project_name, description, manager_id) VALUES (102, 'Project Beta', 'Second project', 1)");

        jdbcTemplate.execute("INSERT INTO TASKS_V6 (id, title, description, status, priority, project_id, assigned_to_id) VALUES (1001, 'Task 1', 'Description 1', 'TODO', 'HIGH', 101, 2)");
        jdbcTemplate.execute("INSERT INTO TASKS_V6 (id, title, description, status, priority, project_id, assigned_to_id) VALUES (1002, 'Task 2', 'Description 2', 'IN_PROGRESS', 'MEDIUM', 101, 2)");
        jdbcTemplate.execute("INSERT INTO TASKS_V6 (id, title, description, status, priority, project_id, assigned_to_id) VALUES (1003, 'Task 3', 'Description 3', 'DONE', 'LOW', 102, 3)");
        jdbcTemplate.execute("INSERT INTO TASKS_V6 (id, title, description, status, priority, project_id, assigned_to_id) VALUES (1004, 'Task 4', 'Description 4', 'TODO', 'HIGH', 102, 1)");

        jdbcTemplate.execute("INSERT INTO TASK_LOGS_V6 (id, old_status, new_status, changed_by_id, task_id) VALUES (2001, 'TODO', 'IN_PROGRESS', 1, 1002)");
        jdbcTemplate.execute("INSERT INTO TASK_LOGS_V6 (id, old_status, new_status, changed_by_id, task_id) VALUES (2002, 'IN_PROGRESS', 'DONE', 2, 1003)");
    }
}