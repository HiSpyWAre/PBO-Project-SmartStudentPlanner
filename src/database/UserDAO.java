package database;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * UserDAO - Data Access Object untuk operasi database User
 */
public class UserDAO {
    private DatabaseManager dbManager;
    
    public UserDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    /**
     * Save atau update user ke database
     * Returns user ID
     */
    public int saveUser(UserProfile user) {
        try {
            Connection conn = dbManager.getConnection();
            
            // Check if user exists
            String checkSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, user.getUsername());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update existing user
                int userId = rs.getInt("id");
                updateUser(userId, user);
                rs.close();
                checkStmt.close();
                return userId;
            } else {
                // Insert new user
                rs.close();
                checkStmt.close();
                
                String insertSql = """
                    INSERT INTO users (username, xp, level, streak, last_activity_date)
                    VALUES (?, ?, ?, ?, ?)
                """;
                
                PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, user.getUsername());
                stmt.setInt(2, user.getXP());
                stmt.setInt(3, user.getLevel());
                stmt.setInt(4, user.getStreak());
                stmt.setString(5, LocalDate.now().toString());
                
                stmt.executeUpdate();
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                int userId = generatedKeys.getInt(1);
                
                generatedKeys.close();
                stmt.close();
                
                // Save achievements
                saveAchievements(userId, user.getAchievements());
                
                System.out.println("✅ User saved: " + user.getUsername() + " (ID: " + userId + ")");
                return userId;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to save user!");
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Update existing user
     */
    public void updateUser(int userId, UserProfile user) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                UPDATE users 
                SET xp = ?, level = ?, streak = ?, last_activity_date = ?
                WHERE id = ?
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getXP());
            stmt.setInt(2, user.getLevel());
            stmt.setInt(3, user.getStreak());
            stmt.setString(4, LocalDate.now().toString());
            stmt.setInt(5, userId);
            
            stmt.executeUpdate();
            stmt.close();
            
            // Update achievements
            saveAchievements(userId, user.getAchievements());
            
            System.out.println("✅ User updated: " + user.getUsername());
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to update user!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load user from database
     */
    public UserProfile loadUser(String username) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                int xp = rs.getInt("xp");
                int level = rs.getInt("level");
                int streak = rs.getInt("streak");
                
                UserProfile user = new UserProfile(username);
                
                // Set private fields via reflection or use setter methods
                // For now, we'll need to add XP to reach the correct level
                while (user.getLevel() < level) {
                    user.addXP(user.getLevel() * 100);
                }
                // Adjust XP to match
                if (user.getXP() != xp) {
                    int diff = xp - user.getXP();
                    if (diff > 0) user.addXP(diff);
                }
                
                // Load achievements
                loadAchievements(userId, user);
                
                // Load productivity history
                loadProductivityHistory(userId, user);
                
                rs.close();
                stmt.close();
                
                System.out.println("✅ User loaded: " + username + " (Level " + level + ", " + xp + " XP)");
                return user;
            }
            
            rs.close();
            stmt.close();
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to load user!");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Save achievements to database
     */
    private void saveAchievements(int userId, List<Achievement> achievements) {
        try {
            Connection conn = dbManager.getConnection();
            
            // Delete existing achievements
            String deleteSql = "DELETE FROM achievements WHERE user_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();
            deleteStmt.close();
            
            // Insert achievements
            String insertSql = """
                INSERT INTO achievements (user_id, name, description, xp_reward, unlocked, unlocked_date)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            for (Achievement achievement : achievements) {
                stmt.setInt(1, userId);
                stmt.setString(2, achievement.getName());
                stmt.setString(3, achievement.getDescription());
                stmt.setInt(4, achievement.getXpReward());
                stmt.setInt(5, achievement.isUnlocked() ? 1 : 0);
                stmt.setString(6, achievement.getUnlockedDate() != null ? 
                    achievement.getUnlockedDate().toString() : null);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to save achievements!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load achievements from database
     */
    private void loadAchievements(int userId, UserProfile user) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT * FROM achievements WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            // Clear existing achievements
            user.getAchievements().clear();
            
            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                int xpReward = rs.getInt("xp_reward");
                boolean unlocked = rs.getInt("unlocked") == 1;
                
                Achievement achievement = new Achievement(name, description, xpReward, unlocked);
                
                // If unlocked, set the date
                if (unlocked && rs.getString("unlocked_date") != null) {
                    // Note: You may need to add a setter for unlocked_date in Achievement class
                    achievement.unlock();
                }
                
                user.getAchievements().add(achievement);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to load achievements!");
            e.printStackTrace();
        }
    }
    
    /**
     * Save productivity record
     */
    public void saveProductivity(int userId, LocalDate date, int minutes) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                INSERT OR REPLACE INTO productivity_history (user_id, date, minutes)
                VALUES (?, ?, ?)
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, date.toString());
            stmt.setInt(3, minutes);
            
            stmt.executeUpdate();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to save productivity!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load productivity history
     */
    private void loadProductivityHistory(int userId, UserProfile user) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT * FROM productivity_history WHERE user_id = ? ORDER BY date DESC LIMIT 30";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                int minutes = rs.getInt("minutes");
                
                // Add to user's productivity history
                user.getProductivityHistory().put(date, minutes);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to load productivity history!");
            e.printStackTrace();
        }
    }
    
    /**
     * Save Pomodoro session
     */
    public void savePomodoroSession(int userId, PomodoroSession session) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                INSERT INTO pomodoro_sessions (user_id, start_time, end_time, duration, task_name, completed)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, session.getStartTime().toString());
            stmt.setString(3, session.getEndTime() != null ? session.getEndTime().toString() : null);
            stmt.setInt(4, session.getDuration());
            stmt.setString(5, session.getTaskName());
            stmt.setInt(6, session.isCompleted() ? 1 : 0);
            
            stmt.executeUpdate();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to save Pomodoro session!");
            e.printStackTrace();
        }
    }
    
    /**
     * Get total Pomodoro sessions count
     */
    public int getTotalPomodoroSessions(int userId) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT COUNT(*) as count FROM pomodoro_sessions WHERE user_id = ? AND completed = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            int count = rs.getInt("count");
            rs.close();
            stmt.close();
            
            return count;
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to get Pomodoro sessions count!");
            e.printStackTrace();
            return 0;
        }
    }
}