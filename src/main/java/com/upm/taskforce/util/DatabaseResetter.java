package com.upm.taskforce.util;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Scanner;

/**
 * Database Reset Utility
 * 
 * Provides functionality to completely reset the database by dropping all tables.
 * Use with caution - this will delete all data!
 * 
 * Usage:
 * Method 1: Add parameter --reset-database=true when starting the application
 * Method 2: Type RESET (case-sensitive) and press Enter after application starts
 * 
 * After reset, you must restart the application for JPA to recreate the table structure.
 */
@Component
public class DatabaseResetter implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseResetter.class);

    private final EntityManager entityManager;

    /**
     * Constructs the DatabaseResetter with the required EntityManager.
     *
     * @param entityManager JPA EntityManager for database operations
     */
    public DatabaseResetter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Checks startup arguments and initializes the reset listener thread.
     * If reset parameter is detected, triggers the database reset process.
     *
     * @param args command line arguments
     */
    @Override
    public void run(String... args) {
        // Check if reset parameter is present
        boolean shouldReset = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--reset-database=true") || 
                arg.equalsIgnoreCase("reset-database=true")) {
                shouldReset = true;
                break;
            }
        }

        if (!shouldReset) {
            logger.info("Database reset not requested on startup.");
            logger.info("To reset database, add parameter: --reset-database=true");
            logger.info("Or after startup, type RESET (case-sensitive) and press Enter");
            
            // Start background thread to listen for reset command
            startResetListener();
            return;
        }

        // Trigger reset via parameter
        resetDatabaseWithConfirmation();
    }

    /**
     * Starts a daemon thread that listens for console input.
     * When "RESET" is entered, it triggers the database reset process.
     * Also accepts "exit" or "quit" to stop the listener.
     */
    private void startResetListener() {
        Thread resetThread = new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine().trim();
                    if (input.equals("RESET")) {
                        logger.info("Received RESET command from console!");
                        resetDatabaseWithConfirmation();
                    } else if (input.equalsIgnoreCase("exit") || 
                              input.equalsIgnoreCase("quit")) {
                        break;
                    }
                }
            } catch (Exception e) {
                logger.debug("Reset listener thread stopped");
            }
        });
        resetThread.setDaemon(true);
        resetThread.setName("DatabaseResetListener");
        resetThread.start();
    }

    /**
     * Handles the database reset process with user confirmation.
     * In interactive environments, asks for YES confirmation.
     * In non-interactive environments, waits 5 seconds before proceeding.
     */
    private void resetDatabaseWithConfirmation() {
        // Warn the user about the irreversible operation
        logger.warn("========================================");
        logger.warn("!!! DATABASE RESET REQUESTED !!!");
        logger.warn("This will DELETE ALL TABLES AND DATA from the database!");
        logger.warn("========================================");

        // Request confirmation in interactive environments
        if (System.console() != null) {
            System.out.print("Are you sure you want to reset the database? (YES to confirm): ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim();
            
            if (!input.equalsIgnoreCase("YES")) {
                logger.info("Database reset cancelled by user.");
                return;
            }
        } else {
            logger.warn("Non-interactive environment, proceeding with reset in 5 seconds...");
            logger.warn("Press Ctrl+C NOW to cancel!");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("Database reset cancelled.");
                return;
            }
        }

        // Execute the actual reset
        resetDatabase();
    }

    /**
     * Performs the actual database reset by dropping all tables in the correct order.
     * Order is important to avoid foreign key constraint violations.
     * After dropping tables, JPA will recreate them on application restart.
     *
     * @throws RuntimeException if database operations fail
     */
    @Transactional
    public void resetDatabase() {
        logger.info("Starting database reset...");

        try {
            // IMPORTANT: Drop order matters - tables with foreign keys must be dropped first
            String[] tables = {
                "TASK_LOGS_V6",   // Task logs (references tasks and users)
                "TASKS_V6",       // Tasks (references projects and users)
                "PROJECTS_V6",    // Projects (references users)
                "USERS_V6"        // Users (parent table)
            };

            // Disable foreign key constraint checking (Oracle specific approach)
            try {
                entityManager.createNativeQuery("SET CONSTRAINTS ALL DEFERRED").executeUpdate();
            } catch (Exception e) {
                logger.debug("Could not disable constraints (may not be necessary)");
            }

            // Drop each table individually
            for (String tableName : tables) {
                try {
                    // First attempt to disable constraints for the table
                    try {
                        entityManager.createNativeQuery("ALTER TABLE " + tableName + " DISABLE CONSTRAINT ALL").executeUpdate();
                    } catch (Exception e) {
                        logger.debug("Could not disable constraints for {}", tableName);
                    }

                    // Drop the table with CASCADE to remove related constraints
                    entityManager.createNativeQuery("DROP TABLE " + tableName + " CASCADE CONSTRAINTS").executeUpdate();
                    logger.info("Dropped table: {}", tableName);
                } catch (Exception e) {
                    // Table not existing is expected for a clean database, skip
                    logger.debug("Table {} does not exist, skipping drop", tableName);
                }
            }

            logger.info("========================================");
            logger.info("Database reset completed successfully!");
            logger.info("All tables have been dropped.");
            logger.info("========================================");
            logger.warn("IMPORTANT: Please RESTART the application now!");
            logger.warn("JPA will automatically recreate the tables and sequences on startup.");
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("Error during database reset", e);
            throw new RuntimeException("Database reset failed", e);
        }
    }
}
