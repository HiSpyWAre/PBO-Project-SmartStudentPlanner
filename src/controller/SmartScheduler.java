package controller;

import java.time.LocalDateTime;
import java.util.List;

import model.*;
import model.Task;

// Smart Scheduler - generates optimal study schedules
class SmartScheduler {
    private TaskManager taskManager;
    private UserProfile userProfile;
    
    public SmartScheduler(TaskManager taskManager, UserProfile userProfile) {
        this.taskManager = taskManager;
        this.userProfile = userProfile;
    }
    
    public List<ScheduledBlock> generateSchedule(int days) {
        List<ScheduledBlock> schedule = new java.util.ArrayList<>();
        List<Task> pendingTasks = taskManager.getTasksSortedByUrgency();
        
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime = currentTime.plusDays(days);
        
        // Simple greedy scheduling algorithm
        for (Task task : pendingTasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) continue;
            if (!task.canStart()) continue; // Check dependencies
            
            int hoursNeeded = task.getEstimatedHours() - task.getActualHours();
            
            // Schedule in 2-hour blocks during optimal hours (9 AM - 9 PM)
            while (hoursNeeded > 0 && currentTime.isBefore(endTime)) {
                if (isProductiveHour(currentTime)) {
                    int blockHours = Math.min(2, hoursNeeded);
                    ScheduledBlock block = new ScheduledBlock(
                        task,
                        currentTime,
                        currentTime.plusHours(blockHours)
                    );
                    schedule.add(block);
                    hoursNeeded -= blockHours;
                    currentTime = currentTime.plusHours(blockHours);
                } else {
                    currentTime = currentTime.plusHours(1);
                }
            }
        }
        
        return schedule;
    }
    
    public List<Task> getRecommendedTasks(int count) {
        List<Task> urgentTasks = taskManager.getTasksSortedByUrgency();
        
        // Filter by what can be started now and is most urgent
        return urgentTasks.stream()
            .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
            .filter(Task::canStart)
            .limit(count)
            .toList();
    }
    
    private boolean isProductiveHour(LocalDateTime time) {
        int hour = time.getHour();
        return hour >= 9 && hour <= 21; // 9 AM to 9 PM
    }
}

// Scheduled block for calendar display
class ScheduledBlock {
    private Task task;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public ScheduledBlock(Task task, LocalDateTime startTime, LocalDateTime endTime) {
        this.task = task;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public Task getTask() { return task; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    
    public int getDurationHours() {
        return (int) java.time.Duration.between(startTime, endTime).toHours();
    }
}