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
 * All task operations are logged for security and audit purposes.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskLogRepository taskLogRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * Creates a new task with TODO status.
     * Logs the task creation with project and assignee information.
     *
     * @param title The task title
     * @param description The task description
     * @param priority The task priority (HIGH, MEDIUM, LOW)
     * @param projectId The ID of the project this task belongs to
     * @param assigneeId The ID of the user assigned to this task (optional)
     * @return The created Task entity
     */
    @Transactional
    public Task createTask(String title, String description, String priority,
                           Long projectId, Long assigneeId) {
        Project project = projectService.getProjectById(projectId);
        User assignee = null;
        if (assigneeId != null) {
            assignee = userService.findUserById(assigneeId);
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus("TODO");
        task.setProject(project);
        task.setAssignee(assignee);
        Task savedTask = taskRepository.save(task);
        String assigneeInfo = assignee != null ? assignee.getUsername() : "unassigned";
        logger.info("Task created: ID={}, Title={}, Project={}, Assignee={}, Priority={}",
                    savedTask.getId(), title, project.getProjectName(), assigneeInfo, priority);
        return savedTask;
    }

    /**
     * Returns a paginated list of all tasks.
     *
     * @param pageable Pagination and sorting information
     * @return A Page of Task entities
     */
    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAllWithDetails(pageable);
    }

    /**
     * Returns a paginated list of tasks based on the user's role.
     * Admins see all tasks; employees see only their assigned tasks.
     *
     * @param user The user requesting the tasks
     * @param pageable Pagination and sorting information
     * @return A Page of Task entities
     */
    public Page<Task> getTasksForUser(User user, Pageable pageable) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return taskRepository.findAllWithDetails(pageable);
        } else {
            return taskRepository.findByAssigneeId(user.getId(), pageable);
        }
    }

    /**
     * Returns a single task by ID.
     *
     * @param id The task ID
     * @return The Task entity if found
     * @throws IllegalArgumentException if task not found
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found: ID={}", id);
                    return new IllegalArgumentException("Task not found with id: " + id);
                });
    }

    /**
     * Updates task status and records an audit log entry.
     * The operation is atomic: status update and log persist together.
     * Logs the status change with actor information.
     *
     * @param taskId The ID of the task to update
     * @param newStatus The new status value
     * @param actorUsername The username of the user making the status change
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

        TaskLog log = new TaskLog();
        log.setTask(task);
        log.setChangedBy(actor);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        taskLogRepository.save(log);

        logger.info("Task status updated: ID={}, OldStatus={}, NewStatus={}, ChangedBy={}",
                    taskId, oldStatus, newStatus, actor.getUsername());
    }

    /**
     * Deletes a task by ID.
     * Logs the deletion for audit purposes.
     *
     * @param taskId The ID of the task to delete
     * @throws IllegalArgumentException if task not found
     */
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            logger.warn("Deletion attempted on non-existent task: ID={}", taskId);
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }
        taskRepository.deleteById(taskId);
        logger.info("Task deleted: ID={}", taskId);
    }

    /**
     * Updates the core details of a task.
     * This is an admin-only operation.
     *
     * @param updatedTask The task object with updated fields from the form
     */
    @Transactional
    public void updateTask(Task updatedTask) {
        Task existingTask = taskRepository.findById(updatedTask.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid task ID: " + updatedTask.getId()));

        // Update fields
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setProject(updatedTask.getProject());
        existingTask.setAssignee(updatedTask.getAssignee());

        taskRepository.save(existingTask);
        logger.info("Task details updated for ID: {}", existingTask.getId());
    }
}