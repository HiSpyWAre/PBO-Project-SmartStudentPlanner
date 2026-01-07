package database;

import java.sql.*;
import java.io.File;

/**
 * DatabaseManager - Singleton class untuk mengelola koneksi database SQLite
 * Handles connection pooling, schema creation, and database initialization
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private static final String DB_NAME = "studyplanner.db";
    private static final String DB_DIR = ".smartstudyplanner";
    
    // Private constructor untuk singleton pattern
    private DatabaseManager() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create database directory in user home
            String userHome = System.getProperty("user.home");
            File dbDir = new File(userHome, DB_DIR);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            
            // Database file path
            String dbPath = new File(dbDir, DB_NAME).getAbsolutePath();
            String url = "jdbc:sqlite:" + dbPath;
            
            // Create connection
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true); // Auto-commit untuk immediate saves
            
            System.out.println("✅ Database connected: " + dbPath);
            
            // Initialize schema
            initializeSchema();
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC driver not found!");
            System.err.println("Please add sqlite-jdbc dependency to your project.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed, reconnect if needed
            if (connection == null || connection.isClosed()) {
                String userHome = System.getProperty("user.home");
                File dbDir = new File(userHome, DB_DIR);
                String dbPath = new File(dbDir, DB_NAME).getAbsolutePath();
                String url = "jdbc:sqlite:" + dbPath;
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to get database connection!");
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Initialize database schema - create all tables
     */
    private void initializeSchema() {
        try {
            Statement stmt = connection.createStatement();
            
            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    xp INTEGER DEFAULT 0,
                    level INTEGER DEFAULT 1,
                    streak INTEGER DEFAULT 0,
                    last_activity_date TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Tasks table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT,
                    task_type TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    created_date TEXT NOT NULL,
                    status TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    estimated_hours INTEGER NOT NULL,
                    actual_hours INTEGER DEFAULT 0,
                    subject TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);
            
            // Achievements table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS achievements (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    xp_reward INTEGER NOT NULL,
                    unlocked INTEGER DEFAULT 0,
                    unlocked_date TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);
            
            // Productivity history table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS productivity_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    minutes INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE(user_id, date)
                )
            """);
            
            // Pomodoro sessions table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pomodoro_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    start_time TEXT NOT NULL,
                    end_time TEXT,
                    duration INTEGER NOT NULL,
                    task_name TEXT,
                    completed INTEGER DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);
            
            // Flashcard decks table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS decks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    category TEXT DEFAULT 'General',
                    created_date TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);
            
            // Flashcards table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS flashcards (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    deck_id INTEGER NOT NULL,
                    question TEXT NOT NULL,
                    answer TEXT NOT NULL,
                    ease_factor INTEGER DEFAULT 2500,
                    repetitions INTEGER DEFAULT 0,
                    interval INTEGER DEFAULT 0,
                    next_review TEXT NOT NULL,
                    created_date TEXT NOT NULL,
                    last_reviewed TEXT,
                    total_reviews INTEGER DEFAULT 0,
                    correct_count INTEGER DEFAULT 0,
                    FOREIGN KEY (deck_id) REFERENCES decks(id) ON DELETE CASCADE
                )
            """);
            
            // Create indices for better performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_achievements_user_id ON achievements(user_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_productivity_user_date ON productivity_history(user_id, date)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_decks_user_id ON decks(user_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_flashcards_deck_id ON flashcards(deck_id)");
            
            stmt.close();
            System.out.println("✅ Database schema initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize database schema!");
            e.printStackTrace();
        }
    }
    
    /**
     * Check if database is empty (first run)
     */
    public boolean isDatabaseEmpty() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            int count = rs.getInt("count");
            rs.close();
            stmt.close();
            return count == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
    
    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to close database connection!");
            e.printStackTrace();
        }
    }
    
    /**
     * Execute a query and return ResultSet
     */
    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }
    
    /**
     * Execute an update (INSERT, UPDATE, DELETE)
     */
    public int executeUpdate(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        int result = stmt.executeUpdate(query);
        stmt.close();
        return result;
    }
    
    /**
     * Backup database to a specified location
     */
    public void backupDatabase(String backupPath) {
        try {
            String userHome = System.getProperty("user.home");
            File dbDir = new File(userHome, DB_DIR);
            File sourceFile = new File(dbDir, DB_NAME);
            File destFile = new File(backupPath);
            
            java.nio.file.Files.copy(
                sourceFile.toPath(), 
                destFile.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            System.out.println("✅ Database backed up to: " + backupPath);
        } catch (Exception e) {
            System.err.println("❌ Backup failed!");
            e.printStackTrace();
        }
    }
    
    /**
     * Get database file path
     */
    public String getDatabasePath() {
        String userHome = System.getProperty("user.home");
        File dbDir = new File(userHome, DB_DIR);
        return new File(dbDir, DB_NAME).getAbsolutePath();
    }
}