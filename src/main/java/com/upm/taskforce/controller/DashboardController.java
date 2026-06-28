package com.upm.taskforce.controller;

import com.upm.taskforce.entity.Task;
import com.upm.taskforce.entity.User;
import com.upm.taskforce.service.TaskService;
import com.upm.taskforce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles the main dashboard view after login.
 * Displays tasks based on user role with pagination.
 */
@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    /**
     * Loads the dashboard with a paginated list of tasks appropriate for the current user.
     * Admins see all tasks; employees see only their assigned tasks.
     * Logs user access for security audit purposes.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        Page<Task> taskPage = taskService.getTasksForUser(currentUser, pageable);
        model.addAttribute("taskPage", taskPage);
        model.addAttribute("currentUser", username);
        logger.debug("Dashboard accessed by user: {} with page: {}", username, pageable.getPageNumber());
        return "dashboard";
    }
}