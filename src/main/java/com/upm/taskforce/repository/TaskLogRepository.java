package com.upm.taskforce.repository;

import com.upm.taskforce.entity.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Data access for TaskLog entity.
 * Provides historical log retrieval for audit display.
 */
@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    /**
     * Returns all logs for a specific task, ordered by most recent change first.
     */
    List<TaskLog> findByTaskIdOrderByChangeTimeDesc(Long taskId);
}