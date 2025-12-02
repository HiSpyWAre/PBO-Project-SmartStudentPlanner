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
    
    // Operasi CRUD (Create, Read, Update, Delete) untuk tugas
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
    
    // Metode untuk mendapatkan daftar tugas dengan berbagai filter dan sorting
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
    // Mendapatkan tugas berdasarkan status
    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.stream()
            .filter(t -> t.status == status)
            .toList();
    }
    
    // Mendapatkan tugas yang akan datang dalam beberapa hari ke depan
    public List<Task> getUpcomingTasks(int days) {
        LocalDateTime future = LocalDateTime.now().plusDays(days);
        return tasks.stream() 
            .filter(t -> t.status != TaskStatus.COMPLETED) // hanya tugas yang belum selesai
            .filter(t -> t.dueDate.isBefore(future)) // due dalam rentang waktu tertentu
            .sorted((a, b) -> a.dueDate.compareTo(b.dueDate)) // sort by due date
            .toList(); // return sebagai list
    }
    
    // Mendapatkan tugas yang diurutkan berdasarkan skor urgensi
    public List<Task> getTasksSortedByUrgency() {
        return tasks.stream()
            .filter(t -> t.status != TaskStatus.COMPLETED) 
            .sorted((a, b) -> Double.compare(b.calculateUrgencyScore(), a.calculateUrgencyScore()))
            .toList();
    }
    
    // total estimasi jam untuk semua tugas yang belum selesai
    public int getTotalEstimatedHours() {
        return tasks.stream()
            .filter(t -> t.status != TaskStatus.COMPLETED)
            .mapToInt(Task::getEstimatedHours)
            .sum();
    }
    
    
    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }
    
    // notify semua observer ketika ada perubahan pada daftar tugas
    private void notifyObservers() {
        observers.forEach(TaskObserver::onTasksChanged);
    }
}

// Observer interface for task changes
interface TaskObserver {
    void onTasksChanged();
}