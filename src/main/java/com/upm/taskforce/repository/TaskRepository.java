package com.upm.taskforce.repository;

import com.upm.taskforce.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Data access for Task entity.
 * Provides CRUD plus queries for filtering by assignee or project.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.project")
    List<Task> findAllWithDetails();

    @Query(value = "SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.project",
           countQuery = "SELECT count(t) FROM Task t")
    Page<Task> findAllWithDetails(Pageable pageable);

    /**
     * Retrieves all tasks assigned to a specific user.
     * Supports personal task lists and load filtering.
     */
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.project WHERE t.assignee.id = :assigneeId")
    List<Task> findByAssigneeId(Long assigneeId);

    @Query(value = "SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.project WHERE t.assignee.id = :assigneeId",
           countQuery = "SELECT count(t) FROM Task t WHERE t.assignee.id = :assigneeId")
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    /**
     * Retrieves all tasks belonging to a given project.
     * Used for project scoping and dashboards.
     */
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.project WHERE t.project.id = :projectId")
    List<Task> findByProjectId(Long projectId);
}