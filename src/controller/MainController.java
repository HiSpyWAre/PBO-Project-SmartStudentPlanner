package controller;

import model.*;
import java.util.List;

/**
 * Main Controller dengan proper XP reward system
 */
public class MainController {
    private TaskManager taskManager;
    private UserProfile userProfile;
    private SmartScheduler scheduler;
    
    public MainController(TaskManager taskManager, UserProfile userProfile) {
        this.taskManager = taskManager;
        this.userProfile = userProfile;
        this.scheduler = new SmartScheduler(taskManager, userProfile);
    }
    
    // ====== TASK OPERATIONS ======
    
    public void createTask(Task task) {
        taskManager.addTask(task);
    }
    
    public void updateTask(Task task) {
        taskManager.updateTask(task);
    }
    
    public void deleteTask(Task task) {
        taskManager.removeTask(task);
    }
    
    /**
     * Complete task dan berikan XP reward
     */
    public void completeTask(Task task) {
        // Cek apakah task sudah completed sebelumnya
        if (task.getStatus() == TaskStatus.COMPLETED) {
            System.out.println("⚠️ Task already completed!");
            return;
        }
        
        // Mark as complete
        task.markComplete();
        taskManager.updateTask(task);
        
        // Calculate XP reward
        int xpReward = calculateXPReward(task);
        System.out.println("💰 Earned " + xpReward + " XP for completing: " + task.getTitle());
        
        // Award XP (ini akan trigger observer notification)
        userProfile.addXP(xpReward);
        userProfile.updateStreak();
        
        // Check achievements
        checkFirstStepsAchievement();
        checkPerfectionistAchievement();
    }
    
    /**
     * Check "First Steps" achievement (complete 1 task)
     */
    private void checkFirstStepsAchievement() {
        Achievement firstSteps = userProfile.getAchievements().stream()
            .filter(a -> a.getName().equals("First Steps"))
            .findFirst()
            .orElse(null);
        
        if (firstSteps != null && !firstSteps.isUnlocked()) {
            if (taskManager.getTasksByStatus(TaskStatus.COMPLETED).size() >= 1) {
                firstSteps.unlock();
                userProfile.addXP(firstSteps.getXpReward());
                System.out.println("🏆 Achievement unlocked: First Steps!");
            }
        }
    }
    
    /**
     * Check "Perfectionist" achievement (complete 10 tasks on time)
     */
    private void checkPerfectionistAchievement() {
        Achievement perfectionist = userProfile.getAchievements().stream()
            .filter(a -> a.getName().equals("Perfectionist"))
            .findFirst()
            .orElse(null);
        
        if (perfectionist != null && !perfectionist.isUnlocked()) {
            long onTimeCount = taskManager.getTasksByStatus(TaskStatus.COMPLETED).stream()
                .filter(t -> t.getActualHours() <= t.getEstimatedHours())
                .count();
            
            if (onTimeCount >= 10) {
                perfectionist.unlock();
                userProfile.addXP(perfectionist.getXpReward());
                System.out.println("🏆 Achievement unlocked: Perfectionist!");
            }
        }
    }
    
    /**
     * Calculate XP reward berdasarkan task type dan priority
     */
    private int calculateXPReward(Task task) {
        int baseXP = 50;
        
        // Priority bonus: LOW=0, MEDIUM=25, HIGH=50, URGENT=75
        int priorityBonus = task.getPriority().ordinal() * 25;
        
        // Type bonus
        int typeBonus = 0;
        if (task instanceof Exam) {
            typeBonus = 100; // Exams give most XP
        } else if (task instanceof Project) {
            typeBonus = 75;
        } else if (task instanceof Assignment) {
            typeBonus = 50;
        }
        
        // Time bonus (completed on time or early)
        int timeBonus = 0;
        if (task.getActualHours() <= task.getEstimatedHours()) {
            timeBonus = 50;
        }
        
        // Difficulty bonus based on estimated hours
        int difficultyBonus = task.getEstimatedHours() * 5;
        
        int totalXP = baseXP + priorityBonus + typeBonus + timeBonus + difficultyBonus;
        
        System.out.println("📊 XP Breakdown:");
        System.out.println("  Base: " + baseXP);
        System.out.println("  Priority (" + task.getPriority() + "): +" + priorityBonus);
        System.out.println("  Type (" + task.getClass().getSimpleName() + "): +" + typeBonus);
        System.out.println("  Time bonus: +" + timeBonus);
        System.out.println("  Difficulty: +" + difficultyBonus);
        System.out.println("  TOTAL: " + totalXP + " XP");
        
        return totalXP;
    }
    
    // ====== SCHEDULING OPERATIONS ======
    
    public List<ScheduledBlock> generateOptimalSchedule(int days) {
        return scheduler.generateSchedule(days);
    }
    
    public List<Task> getRecommendedTasksForNow() {
        return scheduler.getRecommendedTasks(3);
    }
    
    // ====== ANALYTICS ======
    
    public StudyStatistics getStatistics() {
        return new StudyStatistics(userProfile, taskManager);
    }
    
    // ====== GETTERS ======
    
    public TaskManager getTaskManager() {
        return taskManager;
    }
    
    public UserProfile getUserProfile() {
        return userProfile;
    }
}

// package controller;

// import model.*;
// // import java.time.LocalDateTime;
// import java.util.List;

// // Main Controller - menghubungkan model dan view, mengelola logika aplikasi
// public class MainController {
//     private TaskManager taskManager;
//     private UserProfile userProfile;
//     private SmartScheduler scheduler;
    
//     // konstruktor
//     public MainController(TaskManager taskManager, UserProfile userProfile) {
//         this.taskManager = taskManager;
//         this.userProfile = userProfile;
//         this.scheduler = new SmartScheduler(taskManager, userProfile);
//     }
    
//     // Task operations = CRUD dan penyelesaian tugas
//     public void createTask(Task task) {
//         taskManager.addTask(task);
//     }
    
//     public void updateTask(Task task) {
//         taskManager.updateTask(task);
//     }
    
//     public void deleteTask(Task task) {
//         taskManager.removeTask(task);
//     }
    
//     public void completeTask(Task task) {
//         task.markComplete();
//         taskManager.updateTask(task);
        
//         // Award XP berdasarkan tipe tugas dan prioritas
//         int xpReward = calculateXPReward(task);
//         userProfile.addXP(xpReward);
//         userProfile.updateStreak();
//         checkFirstStepsAchievement();

//         System.out.println("Task completed: " + task.getTitle());
//         System.out.println("XP earned: " + xpReward);
//         System.out.println("Total XP: " + userProfile.getXP());
//         System.out.println("Level: " + userProfile.getLevel());

//     }
    
//     private void checkFirstStepsAchievement() {
//     Achievement firstSteps = userProfile.getAchievements().stream()
//         .filter(a -> a.getName().equals("First Steps"))
//         .findFirst()
//         .orElse(null);
    
//     if (firstSteps != null && !firstSteps.isUnlocked()) {
//         if (taskManager.getTasksByStatus(TaskStatus.COMPLETED).size() >= 1) {
//             firstSteps.unlock();
//             userProfile.addXP(firstSteps.getXpReward());
//             System.out.println("Achievement unlocked: First Steps!");
//         }
//     }
// }
//     // method untuk menghitung XP reward 
//     private int calculateXPReward(Task task) {
//         int baseXP = 50;
//         int priorityBonus = task.getPriority().ordinal() * 25;
//         int typeBonus = 0;
//         // tambahan bonus berdasarkan tipe tugas
//         if (task instanceof Exam) {
//             typeBonus = 100;
//         } else if (task instanceof Project) {
//             typeBonus = 75;
// }
//         // bonus jika tugas diselesaikan tepat waktu
//         int timeBonus = task.getActualHours() <= task.getEstimatedHours() ? 50 : 0;
        
//         return baseXP + priorityBonus + typeBonus + timeBonus;
//     }
    
//     // Scheduling operations
//     public List<ScheduledBlock> generateOptimalSchedule(int days) {
//         return scheduler.generateSchedule(days);
//     }
    
//     public List<Task> getRecommendedTasksForNow() {
//         return scheduler.getRecommendedTasks(3);
//     }
    
//     // Analytics = statistik belajar
//     public StudyStatistics getStatistics() {
//         return new StudyStatistics(userProfile, taskManager);
//     }
    
//     // getter untuk task manager
//     public TaskManager getTaskManager() {
//         return taskManager;
//     }
    
//     // getter untuk user profile
//     public UserProfile getUserProfile() {
//         return userProfile;
//     }
// }
