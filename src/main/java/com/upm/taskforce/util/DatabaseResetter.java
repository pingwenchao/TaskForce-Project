package com.upm.taskforce.util;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Scanner;

/**
 * 数据库重置工具
 * 
 * 使用方法：
 * 方法1：运行时添加参数 --reset-database=true
 * 方法2：直接在启动后输入 RESET 命令
 * 
 * 警告：这会删除所有表和数据！
 * 重置后需要重启应用让 JPA 重新创建表结构
 */
@Component
public class DatabaseResetter implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseResetter.class);

    private final EntityManager entityManager;

    public DatabaseResetter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void run(String... args) {
        // 检查是否有重置参数
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
            
            // 启动一个后台线程监听输入
            startResetListener();
            return;
        }

        // 参数方式触发重置
        resetDatabaseWithConfirmation();
    }

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

    private void resetDatabaseWithConfirmation() {
        // 警告用户
        logger.warn("========================================");
        logger.warn("!!! DATABASE RESET REQUESTED !!!");
        logger.warn("This will DELETE ALL TABLES AND DATA from the database!");
        logger.warn("========================================");

        // 如果是交互式环境，请求确认
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

        // 执行重置
        resetDatabase();
    }

    @Transactional
    public void resetDatabase() {
        logger.info("Starting database reset...");

        try {
            // 重要：删除顺序很重要，必须先删除有外键约束的表
            String[] tables = {
                "TASK_LOGS_V6",   // 任务日志（关联任务和用户）
                "TASKS_V6",       // 任务（关联项目和用户）
                "PROJECTS_V6",    // 项目（关联用户）
                "USERS_V6"        // 用户（主表）
            };

            // 禁用外键约束检查（Oracle 方式）
            try {
                entityManager.createNativeQuery("SET CONSTRAINTS ALL DEFERRED").executeUpdate();
            } catch (Exception e) {
                logger.debug("Could not disable constraints (may not be necessary)");
            }

            // 逐个删除表
            for (String tableName : tables) {
                try {
                    // 先尝试禁用表的约束
                    try {
                        entityManager.createNativeQuery("ALTER TABLE " + tableName + " DISABLE CONSTRAINT ALL").executeUpdate();
                    } catch (Exception e) {
                        logger.debug("Could not disable constraints for {}", tableName);
                    }

                    // 删除表（使用 CASCADE CONSTRAINTS 来删除相关约束）
                    entityManager.createNativeQuery("DROP TABLE " + tableName + " CASCADE CONSTRAINTS").executeUpdate();
                    logger.info("Dropped table: {}", tableName);
                } catch (Exception e) {
                    // 表不存在是正常的，忽略异常
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
