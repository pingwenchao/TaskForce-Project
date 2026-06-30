package com.upm.taskforce.service;

import com.upm.taskforce.entity.Project;
import com.upm.taskforce.entity.User;
import com.upm.taskforce.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manages Project CRUD operations.
 * All project operations are logged for audit trail.
 */
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    /**
     * Creates a new project with the specified manager.
     * Logs the project creation with manager information.
     *
     * @param projectName The name of the new project
     * @param description The project description
     * @param managerId The ID of the user managing this project
     * @return The created Project entity
     */
    @Transactional
    public Project createProject(String projectName, String description, Long managerId) {
        User manager = userService.findUserById(managerId);
        Project project = new Project();
        project.setProjectName(projectName);
        project.setDescription(description);
        project.setManager(manager);
        Project savedProject = projectRepository.save(project);
        logger.info("Project created: ID={}, Name={}, Manager={}", savedProject.getId(), projectName, manager.getUsername());
        return savedProject;
    }

    /**
     * Returns a paginated list of all projects.
     *
     * @param pageable Pagination and sorting information
     * @return A Page of Project entities
     */
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAllWithDetails(pageable);
    }

    /**
     * Returns a project by ID.
     *
     * @param id The project ID
     * @return The Project entity if found
     * @throws IllegalArgumentException if project not found
     */
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Project not found: ID={}", id);
                    return new IllegalArgumentException("Project not found with id: " + id);
                });
    }

    /**
     * Deletes a project and all its associated tasks (cascade delete).
     * Logs the deletion for audit purposes.
     *
     * @param projectId The ID of the project to delete
     * @throws IllegalArgumentException if project not found
     */
    @Transactional
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            logger.warn("Deletion attempted on non-existent project: ID={}", projectId);
            throw new IllegalArgumentException("Project not found with id: " + projectId);
        }
        projectRepository.deleteById(projectId);
        logger.info("Project deleted: ID={}", projectId);
    }
}
