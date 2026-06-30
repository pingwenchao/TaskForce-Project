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

    /** When the change was recorded */
    @Column(nullable = false)
    private LocalDateTime changeTime = LocalDateTime.now();

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }

    public User getChangedBy() {
        return changedBy;
    }
    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public String getOldStatus() {
        return oldStatus;
    }
    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangeTime() {
        return changeTime;
    }
    public void setChangeTime(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskLog taskLog = (TaskLog) o;
        return id != null && Objects.equals(id, taskLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}