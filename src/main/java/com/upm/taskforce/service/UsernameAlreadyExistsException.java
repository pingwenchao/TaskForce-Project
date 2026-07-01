package com.upm.taskforce.service;

/**
 * Exception thrown when attempting to register a user with a username that already exists.
 * This is a checked exception alternative that provides clear user feedback for duplicate registrations.
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining the conflict
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}