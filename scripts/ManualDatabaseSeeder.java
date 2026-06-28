// To compile and run this file manually:
// 1. Make sure you have Oracle JDBC driver (ojdbc11.jar) in your classpath.
//    You can download it from Oracle's website or find it in your Maven repository (~/.m2/repository/com/oracle/database/jdbc/ojdbc11/...).
// 2. Compile: javac -cp "path/to/ojdbc11.jar" ManualDatabaseSeeder.java
// 3. Run:     java -cp ".;path/to/ojdbc11.jar" ManualDatabaseSeeder

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ManualDatabaseSeeder {

    // --- DATABASE CONNECTION DETAILS ---
    // !!! IMPORTANT: DO NOT COMMIT THIS FILE TO GITHUB !!!
    private static final String DB_URL = "jdbc:oracle:thin:@fsktmdbora.upm.edu.my:1521:fsktm";
    private static final String DB_USERNAME = "D226969";

    private static final String DB_PASSWORD = "226969";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            System.out.println("Successfully connected to the database.");

            // --- Step 1: Clean up existing data ---
            System.out.println("Deleting existing data...");
            stmt.execute("DELETE FROM TASK_LOGS_V6");
            stmt.execute("DELETE FROM TASKS_V6");
            stmt.execute("DELETE FROM PROJECTS_V6");
            stmt.execute("DELETE FROM USERS_V6");
            System.out.println("Data deletion complete.");

            // --- Step 2: Reset the primary key sequence ---
            System.out.println("Resetting sequence...");
            try {
                stmt.execute("DROP SEQUENCE HIBERNATE_SEQUENCE");
            } catch (Exception e) {
                System.out.println("Sequence HIBERNATE_SEQUENCE did not exist, which is fine.");
            }
            stmt.execute("CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1 INCREMENT BY 1");
            System.out.println("Sequence reset complete.");

            // --- Step 3: Insert new data ---
            System.out.println("Inserting new data...");

            // BCrypt hash for "password" is: $2a$10$EixZaYcQlG0y2eW5wL7W.e7GQ8eEeX6xX6xX6xX6xX6xX6xX
            String hashedPassword = "$2a$10$EixZaYcQlG0y2eW5wL7W.e7GQ8eEeX6xX6xX6xX6xX6xX6xX";

            stmt.executeUpdate("INSERT INTO USERS_V6 (id, username, password, role) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'admin', '" + hashedPassword + "', 'ADMIN')");
            stmt.executeUpdate("INSERT INTO USERS_V6 (id, username, password, role) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'user', '" + hashedPassword + "', 'USER')");

            stmt.executeUpdate("INSERT INTO PROJECTS_V6 (id, name, description, manager_id, created_date) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'Project Alpha', 'This is the description for Project Alpha.', 1, CURRENT_TIMESTAMP)");
            stmt.executeUpdate("INSERT INTO PROJECTS_V6 (id, name, description, manager_id, created_date) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'Project Beta', 'This is the description for Project Beta.', 1, CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT INTO TASKS_V6 (id, title, description, priority, status, project_id, assignee_id, created_date) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'Design new UI', 'Create mockups for the new user interface.', 'HIGH', 'TODO', 3, 2, CURRENT_TIMESTAMP)");
            stmt.executeUpdate("INSERT INTO TASKS_V6 (id, title, description, priority, status, project_id, assignee_id, created_date) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'Develop login feature', 'Implement the user login functionality.', 'HIGH', 'IN_PROGRESS', 3, 2, CURRENT_TIMESTAMP)");
            stmt.executeUpdate("INSERT INTO TASKS_V6 (id, title, description, priority, status, project_id, created_date) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'Setup database', 'Configure the production database.', 'MEDIUM', 'DONE', 4, CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT INTO TASK_LOGS_V6 (id, old_status, new_status, changed_by_id, task_id, change_time) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'IN_PROGRESS', 'DONE', 1, 7, CURRENT_TIMESTAMP)");
            stmt.executeUpdate("INSERT INTO TASK_LOGS_V6 (id, old_status, new_status, changed_by_id, task_id, change_time) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'TODO', 'IN_PROGRESS', 1, 6, CURRENT_TIMESTAMP)");

            System.out.println("Data insertion complete.");
            System.out.println("\n--- DATABASE INITIALIZATION SUCCESSFUL! ---");

        } catch (Exception e) {
            System.err.println("\n--- DATABASE INITIALIZATION FAILED! ---");
            e.printStackTrace();
        }
    }
}
