package com.upm.taskforce.controller;

import com.upm.taskforce.entity.Project;
import com.upm.taskforce.entity.Task;
import com.upm.taskforce.service.ProjectService;
import com.upm.taskforce.service.TaskService;
import com.upm.taskforce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Manages all task-related web requests.
 * Supports create, read, update (including status transitions), and delete operations.
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * Displays the form to create a new task.
     * Accessible to authenticated users.
     */
    @GetMapping("/new")
    public String showAddForm(Model model) {
        // Fetch all projects for the dropdown, not paginated in this context.
        Page<Project> projectPage = projectService.getAllProjects(PageRequest.of(0, 2000));
        model.addAttribute("projects", projectPage.getContent());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("task", new Task()); // Add empty task object for form binding
        return "task-form";
    }

    /**
     * Processes task creation and redirects to dashboard.
     * Validates project and assignee exist before creating task.
     */
    @PostMapping("/add")
    public String addTask(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String priority,
                          @RequestParam Long projectId,
                          @RequestParam(required = false) Long assigneeId) {
        taskService.createTask(title, description, priority, projectId, assigneeId);
        return "redirect:/dashboard";
    }

    /**
     * Displays detail view of a single task including audit logs.
     */
    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "task-detail";
    }

    /**
     * Displays the form to edit an existing task.
     * Accessible only to ADMIN users.
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("projects", projectService.getAllProjects(PageRequest.of(0, 2000)).getContent());
        model.addAttribute("users", userService.getAllUsers());
        return "task-edit-form";
    }

    /**
     * Processes task updates from the edit form.
     * Accessible only to ADMIN users.
     */
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateTask(@ModelAttribute("task") Task task) {
        taskService.updateTask(task);
        return "redirect:/tasks/" + task.getId();
    }

    /**
     * Updates the status of a task and records the change in the audit log.
     * The currently authenticated user is recorded as the actor.
     * All authenticated users can update task status.
     */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String newStatus,
                               Authentication authentication) {
        taskService.updateTaskStatus(id, newStatus, authentication.getName());
        return "redirect:/dashboard";
    }

    /**
     * Deletes a task by ID.
     * Only ADMIN users can delete tasks.
     */
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTask(@RequestParam Long id) {
        taskService.deleteTask(id);
        return "redirect:/dashboard";
    }
}
