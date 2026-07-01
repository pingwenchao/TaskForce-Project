package com.upm.taskforce.config;

import com.upm.taskforce.entity.*;
import com.upm.taskforce.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskLogRepository taskLogRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize if no users exist yet
        if (userRepository.count() > 0) {
            logger.info("Database already contains data - skipping demo data initialization");
            return;
        }

        logger.info("Initializing demo data...");

        // Create users
        User admin = createUser("admin", "Password123", Role.ROLE_ADMIN);
        User user = createUser("user", "Password123", Role.ROLE_EMPLOYEE);
        User test = createUser("test", "Password123", Role.ROLE_EMPLOYEE);

        // Create projects
        Project projectAlpha = createProject("Project Alpha", "First project", admin);
        Project projectBeta = createProject("Project Beta", "Second project", admin);

        // Create tasks
        Task task1 = createTask("Task 1", "Description 1", "HIGH", "TODO", projectAlpha, user);
        Task task2 = createTask("Task 2", "Description 2", "MEDIUM", "IN_PROGRESS", projectAlpha, user);
        Task task3 = createTask("Task 3", "Description 3", "LOW", "DONE", projectBeta, test);
        Task task4 = createTask("Task 4", "Description 4", "HIGH", "TODO", projectBeta, admin);

        // Create task logs
        createTaskLog(task2, null, "IN_PROGRESS", admin);
        createTaskLog(task3, "TODO", "IN_PROGRESS", admin);
        createTaskLog(task3, "IN_PROGRESS", "DONE", user);

        logger.info("Demo data initialization complete!");
        logger.info("You can log in with:");
        logger.info("  Username: admin / Password: Password123 (Admin)");
        logger.info("  Username: user / Password: Password123 (Employee)");
        logger.info("  Username: test / Password: Password123 (Employee)");
    }

    private User createUser(String username, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        String encodedPassword = passwordEncoder.encode(password);
        logger.info("Creating user '{}' with encoded password: {}", username, encodedPassword);
        user.setPassword(encodedPassword);
        user.setRole(role);
        return userRepository.save(user);
    }

    private Project createProject(String name, String description, User manager) {
        Project project = new Project();
        project.setProjectName(name);
        project.setDescription(description);
        project.setManager(manager);
        return projectRepository.save(project);
    }

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

    private TaskLog createTaskLog(Task task, String oldStatus, String newStatus, User changedBy) {
        TaskLog log = new TaskLog();
        log.setTask(task);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setChangedBy(changedBy);
        return taskLogRepository.save(log);
    }
}
