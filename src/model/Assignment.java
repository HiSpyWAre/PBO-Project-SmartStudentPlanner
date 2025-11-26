package model;

import java.time.LocalDateTime;

public class Assignment extends Task {
    private String subject;
    private boolean hasAutoGrading;
    
    // constructor untuk Assignment
    public Assignment(String title, String description, LocalDateTime dueDate, 
                     int estimatedHours, TaskPriority priority) {
        super(title, description, dueDate, estimatedHours, priority);
        this.subject = "";
        this.hasAutoGrading = false;
    }
    
    @Override
    // tingkat/score urgensi tugas yang di-display di dashboard
    public double calculateUrgencyScore() {
        long hoursUntilDue = java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();
        double timeScore = 100.0 / (hoursUntilDue + 1); //Semakin dekat ke deadline, semakin tinggi skornya
        double priorityScore = priority.ordinal() * 25.0; // HIGH = 75, MEDIUM = 50, LOW = 25
        double effortScore = estimatedHours * 5.0; // tugas lama lebih urgent
        
        return timeScore + priorityScore + effortScore;
    }
    
    // getters dan setters
    public String getSubject() { 
        return subject; 
    }
    public void setSubject(String subject) { 
        this.subject = subject; 
    }
}
