package model;

import java.util.*;
import java.time.LocalDateTime;

public class Project extends Task {
    private List<Task> subtasks;
    private LocalDateTime startDate;
    
    public Project(String title, String description, LocalDateTime dueDate, 
                   int estimatedHours, TaskPriority priority) {
        super(title, description, dueDate, estimatedHours, priority);
        this.subtasks = new ArrayList<>();
        this.startDate = LocalDateTime.now();
    }
    
    @Override
    public double calculateUrgencyScore() {
        long hoursUntilDue = java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();
        double timeScore = 80.0 / (hoursUntilDue + 1);
        double completionScore = (1.0 - getCompletionPercentage() / 100.0) * 40.0;
        double priorityScore = priority.ordinal() * 20.0;
        
        return timeScore + completionScore + priorityScore;
    }
    
    public void addSubtask(Task task) {
        subtasks.add(task);
    }
    
    public double getCompletionPercentage() {
        if (subtasks.isEmpty()) {
            return actualHours >= estimatedHours ? 100.0 : 
                   (double)actualHours / estimatedHours * 100.0;
        }
        
        long completed = subtasks.stream()
            .filter(t -> t.status == TaskStatus.COMPLETED)
            .count();
        return (double)completed / subtasks.size() * 100.0;
    }
    
    public List<Task> getSubtasks() { return subtasks; }
}
