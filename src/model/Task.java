package model;

import java.time.LocalDateTime;
import java.util.*;

// Abstract base class untuk semua jenis tugas
public abstract class Task {
    // idCounter untuk generate unique IDs : setiap tugas punya identifier beda
    private static int idCounter = 0;
    
    // menggunakan protected agar variabel bisa diakses oleh subclass
    protected int id;
    protected String title;
    protected String description;
    protected LocalDateTime dueDate;
    protected LocalDateTime createdDate;
    protected TaskStatus status;
    protected TaskPriority priority;
    protected int estimatedHours;
    protected int actualHours;
    protected List<String> tags; // label/kategori untuk tugas
    protected List<Task> dependencies; // tugas yang harus diselesaikan sebelum tugas lain bisa dimulai
    
    public Task(String title, String description, LocalDateTime dueDate, int estimatedHours, TaskPriority priority) {
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
    
    // Abstract method - beda jenis tugas meng-kategorikan score urgency nya
    public abstract double calculateUrgencyScore();
    
    // Common methods untuk semua tugas: 
    // method untuk menandai as complete
    public void markComplete() {
        this.status = TaskStatus.COMPLETED;
    }
    
    // method untuk menambahkan tag, Nb: depedencies tdk diimplementasi di UI
    public void addDependency(Task task) {
        dependencies.add(task);
    }
    
    // method untuk mengecek apakah tugas bisa dimulai (semua dependencies sudah complete)
    public boolean canStart() {
        return dependencies.stream().allMatch(t -> t.status == TaskStatus.COMPLETED);
    }
    
    // method untuk mengupdate status berdasarkan due date dan actual hours(progress)
    public void updateStatus() {
        if (status == TaskStatus.COMPLETED) return;
        
        if (LocalDateTime.now().isAfter(dueDate)) {
            status = TaskStatus.OVERDUE; // kelewatan deadline
        } else if (actualHours > 0) {
            status = TaskStatus.IN_PROGRESS; // sedang dikerjakan
        }
    }
    
    // Getters and setters
    public int getId() { 
        return id; 
    }

    public String getTitle() { 
        return title; 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public LocalDateTime getDueDate() { 
        return dueDate; 
    }

    public void setDueDate(LocalDateTime dueDate) { 
        this.dueDate = dueDate; 
    }

    public LocalDateTime getCreatedDate() { 
        return createdDate; 
    }

    public TaskStatus getStatus() { 
        return status; 
    }

    public void setStatus(TaskStatus status) { 
        this.status = status; 
    }

    public TaskPriority getPriority() { 
        return priority; 
    }

    public void setPriority(TaskPriority priority) { 
        this.priority = priority; 
    }

    public int getEstimatedHours() { 
        return estimatedHours; 
    }

    public void setEstimatedHours(int hours) { 
        this.estimatedHours = hours; 
    }

    public int getActualHours() { 
        return actualHours; 
    }

    public void setActualHours(int hours) { 
        this.actualHours = hours; 
    }

    public List<String> getTags() { 
        return tags; 
    }

    public List<Task> getDependencies() { 
        return dependencies; 
    }
}