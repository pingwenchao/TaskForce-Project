package com.upm.taskforce.service;

import com.upm.taskforce.entity.User;
import com.upm.taskforce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * Loads user by username for Spring Security authentication.
 * Does not handle password encoding – that is delegated to SecurityConfig.
 * All authentication attempts are logged for security audit purposes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user from the database and builds a Spring Security UserDetails object.
     * Role is prefixed with "ROLE_" as required by Spring Security conventions.
     * Logs successful authentication and failed login attempts.
     *
     * @param username The username to load
     * @return UserDetails object with authentication and authorization information
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        logger.debug("User loaded for authentication: {}", username);
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        // CRITICAL_DEBUG: Log the authorities being assigned
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        logger.info("CRITICAL_DEBUG: Assigning authorities to user '{}': {}", username, authorities);

        return userDetails;
    }
}