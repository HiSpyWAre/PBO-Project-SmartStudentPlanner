package model;

import java.time.LocalDateTime;
import java.util.*;

// // Enum for priority levels
// public enum TaskPriority {
//     LOW, MEDIUM, HIGH, URGENT
// }

// // Enum for task status
// enum TaskStatus {
//     TODO, IN_PROGRESS, COMPLETED, OVERDUE
// }

// Abstract base class for all tasks
public abstract class Task {
    private static int idCounter = 0;
    
    protected int id;
    protected String title;
    protected String description;
    protected LocalDateTime dueDate;
    protected LocalDateTime createdDate;
    protected TaskStatus status;
    protected TaskPriority priority;
    protected int estimatedHours;
    protected int actualHours;
    protected List<String> tags;
    protected List<Task> dependencies;
    
    public Task(String title, String description, LocalDateTime dueDate, 
                int estimatedHours, TaskPriority priority) {
        this.id = ++idCounter;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdDate = LocalDateTime.now();
        this.status = TaskStatus.TODO;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
        this.actualHours = 0;
        this.tags = new ArrayList<>();
        this.dependencies = new ArrayList<>();
    }
    
    // Abstract method - different task types calculate urgency differently
    public abstract double calculateUrgencyScore();
    
    // Common methods
    public void markComplete() {
        this.status = TaskStatus.COMPLETED;
    }
    
    public void addDependency(Task task) {
        dependencies.add(task);
    }
    
    public boolean canStart() {
        return dependencies.stream().allMatch(t -> t.status == TaskStatus.COMPLETED);
    }
    
    public void updateStatus() {
        if (status == TaskStatus.COMPLETED) return;
        
        if (LocalDateTime.now().isAfter(dueDate)) {
            status = TaskStatus.OVERDUE;
        } else if (actualHours > 0) {
            status = TaskStatus.IN_PROGRESS;
        }
    }
    
    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public int getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(int hours) { this.estimatedHours = hours; }
    public int getActualHours() { return actualHours; }
    public void setActualHours(int hours) { this.actualHours = hours; }
    public List<String> getTags() { return tags; }
    public List<Task> getDependencies() { return dependencies; }
}