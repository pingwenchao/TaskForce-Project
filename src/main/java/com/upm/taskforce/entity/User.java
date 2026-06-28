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

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Project> getManagedProjects() {
        return managedProjects;
    }
    public void setManagedProjects(Set<Project> managedProjects) {
        this.managedProjects = managedProjects;
    }

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }
    public void setAssignedTasks(Set<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}