package com.upm.taskforce.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for the TaskForce application.
 * Handles common exceptions and provides user-friendly error messages.
 * All exceptions are logged for audit and debugging purposes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles IllegalArgumentException which typically indicates invalid input or resource not found.
     * Logs the error and redirects to dashboard with error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Invalid argument exception: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Invalid input: " + ex.getMessage());
        return new ModelAndView("redirect:/dashboard");
    }

    /**
     * Handles IllegalStateException which typically indicates invalid application state.
     * Logs the error and redirects to login with error message.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleIllegalState(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        logger.error("Illegal state exception: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "System error. Please login again.");
        return new ModelAndView("redirect:/login");
    }

    /**
     * Handles AccessDeniedException when user lacks required permissions.
     * Returns 403 Forbidden error page.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.addObject("message", "You do not have permission to access this resource.");
        return modelAndView;
    }

    /**
     * Catches all other runtime exceptions not explicitly handled.
     * Logs the full stack trace for debugging.
     */
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        logger.error("Unexpected runtime exception", ex);
        redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again later.");
        return new ModelAndView("redirect:/dashboard");
    }

    /**
     * Catches all checked exceptions not explicitly handled.
     * Logs the exception for audit purposes.
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("Unexpected exception", ex);
        redirectAttributes.addFlashAttribute("error", "An error occurred: " + ex.getMessage());
        return new ModelAndView("redirect:/dashboard");
    }
}

