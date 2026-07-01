package com.upm.taskforce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration form data.
 * Includes validation constraints for username, password, and password confirmation.
 */
public class UserRegistrationDto {

    /**
     * Unique username for the new user.
     * Must be between 3 and 50 characters and cannot be empty.
     */
    @NotEmpty(message = "{NotEmpty.userRegistrationDto.username}")
    @Size(min = 3, max = 50, message = "{Size.userRegistrationDto.username}")
    private String username;

    /**
     * Password for the new user.
     * Must be at least 8 characters and cannot be empty.
     */
    @NotEmpty(message = "{NotEmpty.userRegistrationDto.password}")
    @Size(min = 8, message = "{Size.userRegistrationDto.password}")
    private String password;

    /**
     * Password confirmation field.
     * Must match the password field exactly.
     */
    @NotEmpty(message = "{NotEmpty.userRegistrationDto.confirmPassword}")
    private String confirmPassword;

    /**
     * Gets the username.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password confirmation.
     * @return the confirmPassword
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Sets the password confirmation.
     * @param confirmPassword the confirmPassword to set
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
