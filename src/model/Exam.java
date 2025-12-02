package model;

import java.util.*;
import java.time.LocalDateTime;

public class Exam extends Task {
    // private String subject;
    private List<String> topics;
    private int studyHoursCompleted;
    
    public Exam(String title, String description, LocalDateTime dueDate, int estimatedHours) {
        super(title, description, dueDate, estimatedHours, TaskPriority.HIGH);
        this.topics = new ArrayList<>();
        this.studyHoursCompleted = 0;
    }
    
    @Override
    // tingkat/score urgensi ujian yang di-display di dashboard
    public double calculateUrgencyScore() {
        long hoursUntilDue = java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();
        double timeScore = 150.0 / (hoursUntilDue + 1); // multipliernya lebih tinggi untuk score ujian
        double preparednessScore = (1.0 - (double)studyHoursCompleted / estimatedHours) * 50.0;
        
        return timeScore + preparednessScore + 50.0; // Base score untuk exams = 50
    }
    
    // getters dan setters
    public void addStudyHours(int hours) {
        studyHoursCompleted += hours;
        actualHours += hours;
    }
    
    public double getPreparednessPercentage() {
        return Math.min(100.0, (double)studyHoursCompleted / estimatedHours * 100.0);
    }
    
    public List<String> getTopics() { 
        return topics; 
    }

    public int getStudyHoursCompleted() { 
        return studyHoursCompleted; 
    }
}
