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

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public User getManager() {
        return manager;
    }
    public void setManager(User manager) {
        this.manager = manager;
    }

    public Set<Task> getTasks() {
        return tasks;
    }
    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id != null && Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}