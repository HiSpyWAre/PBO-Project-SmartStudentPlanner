package model;

import java.util.*;

// Study statistics
public class StudyStatistics {
    private UserProfile profile;
    private TaskManager taskManager;
    
    // constructor
    public StudyStatistics(UserProfile profile, TaskManager taskManager) {  
        this.profile = profile;
        this.taskManager = taskManager;
    }
    
    // total tugas yang diselesaikan
    public int getCompletedTasksCount() {
        return taskManager.getTasksByStatus(TaskStatus.COMPLETED).size();
    }
    
    // tugas yang diselesaikan tepat waktu
    public int getOnTimeCompletionCount() {
        return (int) taskManager.getTasksByStatus(TaskStatus.COMPLETED).stream()
            .filter(t -> t.getActualHours() <= t.getEstimatedHours())
            .count();
    }
    
    // rata-rata waktu penyelesaian tugas
    public double getAverageTaskCompletionTime() {
        List<Task> completed = taskManager.getTasksByStatus(TaskStatus.COMPLETED);
        if (completed.isEmpty()) return 0.0;
        
        return completed.stream()
            .mapToInt(Task::getActualHours)
            .average()
            .orElse(0.0);
    }
    
    // distribusi tugas berdasarkan prioritas
    public Map<TaskPriority, Integer> getTasksByPriority() {
        Map<TaskPriority, Integer> distribution = new HashMap<>();
        
        // inisialisasi peta dengan prioritas
        for (Task task : taskManager.getAllTasks()) {
            distribution.merge(task.getPriority(), 1, Integer::sum);
        }
        
        return distribution;
    }
    
    // total jam produktif minggu ini
    public int getProductiveHoursThisWeek() {
        return profile.getLastWeekProductivity().values().stream()
            .mapToInt(Integer::intValue)
            .sum() / 60;
    }
}
