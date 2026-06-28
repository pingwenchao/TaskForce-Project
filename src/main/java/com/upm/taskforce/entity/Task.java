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

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignee() {
        return assignee;
    }
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public java.util.List<TaskLog> getLogs() {
        return logs;
    }
    public void setLogs(java.util.List<TaskLog> logs) {
        this.logs = logs;
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
        Task task = (Task) o;
        return id != null && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}