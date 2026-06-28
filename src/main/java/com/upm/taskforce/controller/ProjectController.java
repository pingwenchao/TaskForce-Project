package com.upm.taskforce.controller;

import com.upm.taskforce.entity.Project;
import com.upm.taskforce.service.ProjectService;
import com.upm.taskforce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Manages project-related web requests.
 * Supports list, create, and delete operations.
 * Project creation and deletion are restricted to ADMIN users.
 */
@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * Lists all projects with pagination. Accessible to authenticated users.
     */
    @GetMapping
    public String listProjects(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<Project> projectPage = projectService.getAllProjects(pageable);
        model.addAttribute("projectPage", projectPage);
        return "project-list";
    }

    /**
     * Displays the form to create a new project.
     * Only ADMIN users can access this endpoint.
     */
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("managers", userService.getAllUsers());
        return "project-form";
    }

    /**
     * Processes project creation and redirects to the project list.
     * Only ADMIN users can create new projects.
     * Validates manager exists before project creation.
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addProject(@RequestParam String projectName,
                             @RequestParam String description,
                             @RequestParam Long managerId) {
        projectService.createProject(projectName, description, managerId);
        return "redirect:/projects";
    }

    /**
     * Deletes a project and all its tasks (cascade delete).
     * Only ADMIN users can delete projects.
     */
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProject(@RequestParam Long id) {
        projectService.deleteProject(id);
        return "redirect:/projects";
    }
}