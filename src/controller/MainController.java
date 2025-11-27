package controller;

import model.*;
// import java.time.LocalDateTime;
import java.util.List;

// Main Controller - menghubungkan model dan view, mengelola logika aplikasi
public class MainController {
    private TaskManager taskManager;
    private UserProfile userProfile;
    private SmartScheduler scheduler;
    
    // konstruktor
    public MainController(TaskManager taskManager, UserProfile userProfile) {
        this.taskManager = taskManager;
        this.userProfile = userProfile;
        this.scheduler = new SmartScheduler(taskManager, userProfile);
    }
    
    // Task operations = CRUD dan penyelesaian tugas
    public void createTask(Task task) {
        taskManager.addTask(task);
    }
    
    public void updateTask(Task task) {
        taskManager.updateTask(task);
    }
    
    public void deleteTask(Task task) {
        taskManager.removeTask(task);
    }
    
    public void completeTask(Task task) {
        task.markComplete();
        taskManager.updateTask(task);
        
        // Award XP berdasarkan tipe tugas dan prioritas
        int xpReward = calculateXPReward(task);
        userProfile.addXP(xpReward);
        userProfile.updateStreak();
    }
    
    // method untuk menghitung XP reward 
    private int calculateXPReward(Task task) {
        int baseXP = 50;
        int priorityBonus = task.getPriority().ordinal() * 25;
        int typeBonus = 0;
        // tambahan bonus berdasarkan tipe tugas
        if (task instanceof Exam) {
            typeBonus = 100;
        } else if (task instanceof Project) {
            typeBonus = 75;
}
        // bonus jika tugas diselesaikan tepat waktu
        int timeBonus = task.getActualHours() <= task.getEstimatedHours() ? 50 : 0;
        
        return baseXP + priorityBonus + typeBonus + timeBonus;
    }
    
    // Scheduling operations
    public List<ScheduledBlock> generateOptimalSchedule(int days) {
        return scheduler.generateSchedule(days);
    }
    
    public List<Task> getRecommendedTasksForNow() {
        return scheduler.getRecommendedTasks(3);
    }
    
    // Analytics = statistik belajar
    public StudyStatistics getStatistics() {
        return new StudyStatistics(userProfile, taskManager);
    }
    
    // getter untuk task manager
    public TaskManager getTaskManager() {
        return taskManager;
    }
    
    // getter untuk user profile
    public UserProfile getUserProfile() {
        return userProfile;
    }
}
