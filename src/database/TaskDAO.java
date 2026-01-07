package database;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * TaskDAO - Data Access Object untuk operasi database Task
 */
public class TaskDAO {
    private DatabaseManager dbManager;
    
    public TaskDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    /**
     * Save task ke database
     * Returns generated task ID
     */
    public int saveTask(int userId, Task task) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                INSERT INTO tasks (user_id, title, description, task_type, due_date, created_date,
                                   status, priority, estimated_hours, actual_hours, subject)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.setString(2, task.getTitle());
            stmt.setString(3, task.getDescription());
            stmt.setString(4, getTaskType(task));
            stmt.setString(5, task.getDueDate().toString());
            stmt.setString(6, task.getCreatedDate().toString());
            stmt.setString(7, task.getStatus().toString());
            stmt.setString(8, task.getPriority().toString());
            stmt.setInt(9, task.getEstimatedHours());
            stmt.setInt(10, task.getActualHours());
            stmt.setString(11, getSubject(task));
            
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int taskId = generatedKeys.getInt(1);
            
            generatedKeys.close();
            stmt.close();
            
            System.out.println("✅ Task saved: " + task.getTitle() + " (ID: " + taskId + ")");
            return taskId;
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to save task!");
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Update existing task
     */
    public void updateTask(int taskId, Task task) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                UPDATE tasks 
                SET title = ?, description = ?, due_date = ?, status = ?, 
                    priority = ?, estimated_hours = ?, actual_hours = ?
                WHERE id = ?
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getDueDate().toString());
            stmt.setString(4, task.getStatus().toString());
            stmt.setString(5, task.getPriority().toString());
            stmt.setInt(6, task.getEstimatedHours());
            stmt.setInt(7, task.getActualHours());
            stmt.setInt(8, taskId);
            
            stmt.executeUpdate();
            stmt.close();
            
            System.out.println("✅ Task updated: " + task.getTitle());
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to update task!");
            e.printStackTrace();
        }
    }
    
    /**
     * Delete task from database
     */
    public void deleteTask(int taskId) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskId);
            
            stmt.executeUpdate();
            stmt.close();
            
            System.out.println("✅ Task deleted (ID: " + taskId + ")");
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete task!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load all tasks for a user
     */
    public List<Task> loadAllTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY due_date ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Task task = createTaskFromResultSet(rs);
                if (task != null) {
                    tasks.add(task);
                }
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("✅ Loaded " + tasks.size() + " tasks");
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to load tasks!");
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Load tasks by status
     */
    public List<Task> loadTasksByStatus(int userId, TaskStatus status) {
        List<Task> tasks = new ArrayList<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT * FROM tasks WHERE user_id = ? AND status = ? ORDER BY due_date ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, status.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Task task = createTaskFromResultSet(rs);
                if (task != null) {
                    tasks.add(task);
                }
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to load tasks by status!");
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Create Task object from ResultSet
     */
    private Task createTaskFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String taskType = rs.getString("task_type");
        LocalDateTime dueDate = LocalDateTime.parse(rs.getString("due_date"));
        LocalDateTime createdDate = LocalDateTime.parse(rs.getString("created_date"));
        String statusStr = rs.getString("status");
        String priorityStr = rs.getString("priority");
        int estimatedHours = rs.getInt("estimated_hours");
        int actualHours = rs.getInt("actual_hours");
        
        // Parse enums
        TaskStatus status = TaskStatus.valueOf(statusStr);
        TaskPriority priority = TaskPriority.valueOf(priorityStr);
        
        // Create appropriate task subclass
        Task task = switch (taskType) {
            case "Assignment" -> new Assignment(title, description, dueDate, estimatedHours, priority);
            case "Exam" -> new Exam(title, description, dueDate, estimatedHours);
            case "Project" -> new Project(title, description, dueDate, estimatedHours, priority);
            default -> null;
        };
        
        if (task != null) {
            // Set additional properties
            task.setStatus(status);
            task.setActualHours(actualHours);
            
            // Store database ID in task (you may need to add a field for this)
            // For now, we'll use the task's internal ID counter
        }
        
        return task;
    }
    
    /**
     * Get task type as string
     */
    private String getTaskType(Task task) {
        if (task instanceof Assignment) return "Assignment";
        if (task instanceof Exam) return "Exam";
        if (task instanceof Project) return "Project";
        return "Task";
    }
    
    /**
     * Get subject from task (only for Assignment)
     */
    private String getSubject(Task task) {
        if (task instanceof Assignment) {
            return ((Assignment) task).getSubject();
        }
        return null;
    }
    
    /**
     * Get task completion statistics
     */
    public Map<String, Integer> getTaskStatistics(int userId) {
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            // Total tasks
            String totalSql = "SELECT COUNT(*) as count FROM tasks WHERE user_id = ?";
            PreparedStatement totalStmt = conn.prepareStatement(totalSql);
            totalStmt.setInt(1, userId);
            ResultSet totalRs = totalStmt.executeQuery();
            stats.put("total", totalRs.getInt("count"));
            totalRs.close();
            totalStmt.close();
            
            // Completed tasks
            String completedSql = "SELECT COUNT(*) as count FROM tasks WHERE user_id = ? AND status = 'COMPLETED'";
            PreparedStatement completedStmt = conn.prepareStatement(completedSql);
            completedStmt.setInt(1, userId);
            ResultSet completedRs = completedStmt.executeQuery();
            stats.put("completed", completedRs.getInt("count"));
            completedRs.close();
            completedStmt.close();
            
            // Overdue tasks
            String overdueSql = "SELECT COUNT(*) as count FROM tasks WHERE user_id = ? AND status = 'OVERDUE'";
            PreparedStatement overdueStmt = conn.prepareStatement(overdueSql);
            overdueStmt.setInt(1, userId);
            ResultSet overdueRs = overdueStmt.executeQuery();
            stats.put("overdue", overdueRs.getInt("count"));
            overdueRs.close();
            overdueStmt.close();
            
            // In progress tasks
            String inProgressSql = "SELECT COUNT(*) as count FROM tasks WHERE user_id = ? AND status = 'IN_PROGRESS'";
            PreparedStatement inProgressStmt = conn.prepareStatement(inProgressSql);
            inProgressStmt.setInt(1, userId);
            ResultSet inProgressRs = inProgressStmt.executeQuery();
            stats.put("in_progress", inProgressRs.getInt("count"));
            inProgressRs.close();
            inProgressStmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to get task statistics!");
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Search tasks by title or description
     */
    public List<Task> searchTasks(int userId, String query) {
        List<Task> tasks = new ArrayList<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                SELECT * FROM tasks 
                WHERE user_id = ? AND (title LIKE ? OR description LIKE ?)
                ORDER BY due_date ASC
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Task task = createTaskFromResultSet(rs);
                if (task != null) {
                    tasks.add(task);
                }
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to search tasks!");
            e.printStackTrace();
        }
        
        return tasks;
    }
}