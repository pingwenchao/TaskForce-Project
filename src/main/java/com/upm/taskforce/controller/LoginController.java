package com.upm.taskforce.controller;

import com.upm.taskforce.dto.UserRegistrationDto;
import com.upm.taskforce.service.UserService;
import com.upm.taskforce.service.UsernameAlreadyExistsException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles login page display and user registration.
 * All registration attempts are logged for audit purposes.
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    /**
     * Displays the custom login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Displays the registration form.
     */
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register";
    }

    /**
     * Processes new user registration.
     * On success redirects to login with a success parameter.
     * On failure redirects back to registration with an error parameter.
     * Validates input and logs all registration attempts.
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userDto", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            logger.warn("Registration validation failed: {}", bindingResult.getAllErrors());
            // Return to the form, keeping user input
            return "register";
        }

        try {
            // IMPORTANT: Use the password from the DTO, not a potentially different field
            userService.registerUser(userDto.getUsername(), userDto.getPassword());
            logger.info("User successfully registered: {}", userDto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "registration.success");
            return "redirect:/login";
        } catch (UsernameAlreadyExistsException e) {
            logger.warn("Registration failed for user '{}': {}", userDto.getUsername(), e.getMessage());
            bindingResult.rejectValue("username", "error.userDto", "Username already exists");
            // Return to the form to show the error next to the username field
            return "register";
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed for user '{}': {}", userDto.getUsername(), e.getMessage());
            // Generic error for other validation issues (e.g., password strength)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register?error";
        }
    }
}