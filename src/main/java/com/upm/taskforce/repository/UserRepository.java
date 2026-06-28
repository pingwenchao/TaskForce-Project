package com.upm.taskforce.repository;

import com.upm.taskforce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Data access for User entity.
 * Provides standard CRUD operations and username lookup.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     * Used during authentication and assignment lookup.
     */
    Optional<User> findByUsername(String username);
}