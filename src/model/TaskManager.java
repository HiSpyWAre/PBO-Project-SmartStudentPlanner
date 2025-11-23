package model;

import java.util.*;
import java.time.LocalDateTime;

public class TaskManager {
    private List<Task> tasks;
    private List<TaskObserver> observers;
    
    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.observers = new ArrayList<>();
    }
    
    public void addTask(Task task) {
        tasks.add(task);
        notifyObservers();
    }
    
    public void removeTask(Task task) {
        tasks.remove(task);
        notifyObservers();
    }
    
    public void updateTask(Task task) {
        task.updateStatus();
        notifyObservers();
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.stream()
            .filter(t -> t.status == status)
            .toList();
    }
    
    public List<Task> getUpcomingTasks(int days) {
        LocalDateTime future = LocalDateTime.now().plusDays(days);
        return tasks.stream()
            .filter(t -> t.status != TaskStatus.COMPLETED)
            .filter(t -> t.dueDate.isBefore(future))
            .sorted((a, b) -> a.dueDate.compareTo(b.dueDate))
            .toList();
    }
    
    public List<Task> getTasksSortedByUrgency() {
        return tasks.stream()
            .filter(t -> t.status != TaskStatus.COMPLETED)
            .sorted((a, b) -> Double.compare(b.calculateUrgencyScore(), a.calculateUrgencyScore()))
            .toList();
    }
    
    public int getTotalEstimatedHours() {
        return tasks.stream()
            .filter(t -> t.status != TaskStatus.COMPLETED)
            .mapToInt(Task::getEstimatedHours)
            .sum();
    }
    
    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }
    
    private void notifyObservers() {
        observers.forEach(TaskObserver::onTasksChanged);
    }
}

// Observer interface for task changes
interface TaskObserver {
    void onTasksChanged();
}