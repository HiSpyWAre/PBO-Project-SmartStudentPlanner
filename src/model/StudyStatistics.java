package model;

import java.util.*;

// Study statistics
public class StudyStatistics {
    private UserProfile profile;
    private TaskManager taskManager;
    
    public StudyStatistics(UserProfile profile, TaskManager taskManager) {
        this.profile = profile;
        this.taskManager = taskManager;
    }
    
    public int getCompletedTasksCount() {
        return taskManager.getTasksByStatus(TaskStatus.COMPLETED).size();
    }
    
    public int getOnTimeCompletionCount() {
        return (int) taskManager.getTasksByStatus(TaskStatus.COMPLETED).stream()
            .filter(t -> t.getActualHours() <= t.getEstimatedHours())
            .count();
    }
    
    public double getAverageTaskCompletionTime() {
        List<Task> completed = taskManager.getTasksByStatus(TaskStatus.COMPLETED);
        if (completed.isEmpty()) return 0.0;
        
        return completed.stream()
            .mapToInt(Task::getActualHours)
            .average()
            .orElse(0.0);
    }
    
    public Map<TaskPriority, Integer> getTasksByPriority() {
        Map<TaskPriority, Integer> distribution = new HashMap<>();
        
        for (Task task : taskManager.getAllTasks()) {
            distribution.merge(task.getPriority(), 1, Integer::sum);
        }
        
        return distribution;
    }
    
    public int getProductiveHoursThisWeek() {
        return profile.getLastWeekProductivity().values().stream()
            .mapToInt(Integer::intValue)
            .sum() / 60;
    }
}
