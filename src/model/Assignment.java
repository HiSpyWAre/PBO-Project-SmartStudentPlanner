package model;

import java.time.LocalDateTime;

public class Assignment extends Task {
    private String subject;
    private boolean hasAutoGrading;
    
    public Assignment(String title, String description, LocalDateTime dueDate, 
                     int estimatedHours, TaskPriority priority) {
        super(title, description, dueDate, estimatedHours, priority);
        this.subject = "";
        this.hasAutoGrading = false;
    }
    
    @Override
    public double calculateUrgencyScore() {
        long hoursUntilDue = java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();
        double timeScore = 100.0 / (hoursUntilDue + 1);
        double priorityScore = priority.ordinal() * 25.0;
        double effortScore = estimatedHours * 5.0;
        
        return timeScore + priorityScore + effortScore;
    }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}
