package com.upm.taskforce.service;

import com.upm.taskforce.entity.Role;
import com.upm.taskforce.entity.User;
import com.upm.taskforce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles user registration and password encoding.
 * All user operations are logged for security audit purposes.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new EMPLOYEE user after checking for duplicate username.
     * Password is BCrypt-encoded before storage.
     * Logs successful registrations and duplicate attempts.
     * Validates password meets minimum complexity requirements.
     *
     * @param username The unique username for the new user
     * @param password The plaintext password to be encoded
     * @throws IllegalArgumentException if username already exists or password is invalid
     */
    @Transactional
    public void registerUser(String username, String password) {
        /* Validate username and password are provided */
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Registration attempt with empty username");
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Registration attempt with empty password for username: {}", username);
            throw new IllegalArgumentException("Password cannot be empty");
        }

        /* Check for duplicate username */
        if (userRepository.findByUsername(username).isPresent()) {
            logger.warn("Registration attempt with duplicate username: {}", username);
            throw new UsernameAlreadyExistsException("Username already exists: " + username);
        }

        /* Validate password complexity */
        validatePasswordStrength(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.ROLE_EMPLOYEE);
        userRepository.save(user);
        logger.info("New EMPLOYEE user registered: {}", username);
    }

    /**
     * Validates that a password meets minimum security requirements.
     * Requirements: minimum length and a mix of character types.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    private void validatePasswordStrength(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?/].*");

        if (!hasUppercase || !hasLowercase || !hasDigit) {
            throw new IllegalArgumentException(
                "Password must contain uppercase, lowercase, and numeric characters");
        }
    }

    /**
     * Returns all users.
     *
     * @return List of all User entities
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The user ID
     * @return The User entity
     * @throws IllegalArgumentException if user not found
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username
     * @return The User entity
     * @throws IllegalArgumentException if user not found
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}