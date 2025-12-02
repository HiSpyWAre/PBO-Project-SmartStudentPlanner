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
    // tingkat/score urgensi project yang di-display di dashboard
    public double calculateUrgencyScore() {
        long hoursUntilDue = java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();
        double timeScore = 80.0 / (hoursUntilDue + 1);
        double completionScore = (1.0 - getCompletionPercentage() / 100.0) * 40.0;
        double priorityScore = priority.ordinal() * 20.0;
        
        return timeScore + completionScore + priorityScore;
    }
    
    // methods untuk mengelola subtasks
    public void addSubtask(Task task) {
        subtasks.add(task);
    }

    // menghitung persentase penyelesaian project berdasarkan subtasks
    public double getCompletionPercentage() {
        // jika tidak ada subtasks, gunakan actualHours vs estimatedHours
        if (subtasks.isEmpty()) {
            return actualHours >= estimatedHours ? 100.0 : // jika waktu pengerjaan >= estimasi, dianggap selesai
                   (double)actualHours / estimatedHours * 100.0;
        }

        // hitung progress berdasarkan subtasks yang sudah selesai
        long completed = subtasks.stream()
            .filter(t -> t.status == TaskStatus.COMPLETED)
            .count();
        return (double)completed / subtasks.size() * 100.0;
    }
    
    // getter
    public List<Task> getSubtasks() { 
        return subtasks; 
    }
}
