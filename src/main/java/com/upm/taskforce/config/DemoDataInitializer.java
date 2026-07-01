package com.upm.taskforce.config;

import com.upm.taskforce.entity.*;
import com.upm.taskforce.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializes demo data on application startup if database is empty.
 * Creates sample users, projects, tasks, and task logs for testing purposes.
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoDataInitializer.class);

    /** Repository for user database operations */
    private final UserRepository userRepository;
    /** Repository for project database operations */
    private final ProjectRepository projectRepository;
    /** Repository for task database operations */
    private final TaskRepository taskRepository;
    /** Repository for task log database operations */
    private final TaskLogRepository taskLogRepository;
    /** Encoder for secure password storage */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the demo data initializer with required dependencies.
     *
     * @param userRepository Repository for user data access
     * @param projectRepository Repository for project data access
     * @param taskRepository Repository for task data access
     * @param taskLogRepository Repository for task log data access
     * @param passwordEncoder Encoder for password encryption
     */
    public DemoDataInitializer(UserRepository userRepository,
                               ProjectRepository projectRepository,
                               TaskRepository taskRepository,
                               TaskLogRepository taskLogRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.taskLogRepository = taskLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Runs the demo data initialization on application startup.
     * Only executes if no users exist in the database.
     *
     * @param args Command line arguments
     */
    @Override
    @Transactional
    public void run(String... args) {
        // Skip initialization if database already contains data
        if (userRepository.count() > 0) {
            logger.info("Database already contains data - skipping demo data initialization");
            return;
        }

        logger.info("Initializing demo data...");

        // Create sample users with different roles
        User admin = createUser("admin", "Password123", Role.ROLE_ADMIN);
        User user = createUser("user", "Password123", Role.ROLE_EMPLOYEE);
        User test = createUser("test", "Password123", Role.ROLE_EMPLOYEE);

        // Create sample projects
        Project projectAlpha = createProject("Project Alpha", "First project", admin);
        Project projectBeta = createProject("Project Beta", "Second project", admin);

        // Create sample tasks with various statuses and priorities
        Task task1 = createTask("Task 1", "Description 1", "HIGH", "TODO", projectAlpha, user);
        Task task2 = createTask("Task 2", "Description 2", "MEDIUM", "IN_PROGRESS", projectAlpha, user);
        Task task3 = createTask("Task 3", "Description 3", "LOW", "DONE", projectBeta, test);
        Task task4 = createTask("Task 4", "Description 4", "HIGH", "TODO", projectBeta, admin);

        // Create task log history for audit trail
        createTaskLog(task2, null, "IN_PROGRESS", admin);
        createTaskLog(task3, "TODO", "IN_PROGRESS", admin);
        createTaskLog(task3, "IN_PROGRESS", "DONE", user);

        logger.info("Demo data initialization complete!");
        logger.info("You can log in with:");
        logger.info("  Username: admin / Password: Password123 (Admin)");
        logger.info("  Username: user / Password: Password123 (Employee)");
        logger.info("  Username: test / Password: Password123 (Employee)");
    }

    /**
     * Creates and saves a new user with encoded password.
     *
     * @param username Unique username for login
     * @param password Plain text password to be encoded
     * @param role User role (ADMIN or EMPLOYEE)
     * @return The persisted user entity
     */
    private User createUser(String username, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        String encodedPassword = passwordEncoder.encode(password);
        logger.info("Creating user '{}' with encoded password: {}", username, encodedPassword);
        user.setPassword(encodedPassword);
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Creates and saves a new project.
     *
     * @param name Display name of the project
     * @param description Optional project description
     * @param manager Admin user who manages this project
     * @return The persisted project entity
     */
    private Project createProject(String name, String description, User manager) {
        Project project = new Project();
        project.setProjectName(name);
        project.setDescription(description);
        project.setManager(manager);
        return projectRepository.save(project);
    }

    /**
     * Creates and saves a new task.
     *
     * @param title Short task title
     * @param description Detailed task description
     * @param priority Priority level (HIGH, MEDIUM, LOW)
     * @param status Current status (TODO, IN_PROGRESS, DONE)
     * @param project Parent project this task belongs to
     * @param assignee User assigned to this task (can be null)
     * @return The persisted task entity
     */
    private Task createTask(String title, String description, String priority,
                           String status, Project project, User assignee) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(status);
        task.setProject(project);
        task.setAssignee(assignee);
        return taskRepository.save(task);
    }

    /**
     * Creates and saves a task status change log entry.
     *
     * @param task The task whose status changed
     * @param oldStatus Previous status (null for initial creation)
     * @param newStatus New status after the change
     * @param changedBy User who performed the status change
     * @return The persisted task log entity
     */
    private TaskLog createTaskLog(Task task, String oldStatus, String newStatus, User changedBy) {
        TaskLog log = new TaskLog();
        log.setTask(task);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setChangedBy(changedBy);
        return taskLogRepository.save(log);
    }
}
