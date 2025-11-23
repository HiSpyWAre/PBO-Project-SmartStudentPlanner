package model;

// import java.time.LocalDate;
import java.time.LocalDateTime;
// import java.util.*;


public class PomodoroSession {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration; // in minutes
    private String taskName;
    private boolean completed;
    
    public PomodoroSession(int duration, String taskName) {
        this.duration = duration;
        this.taskName = taskName;
        this.startTime = LocalDateTime.now();
        this.completed = false;
    }
    
    public void complete() {
        this.endTime = LocalDateTime.now();
        this.completed = true;
    }
    
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getDuration() { return duration; }
    public String getTaskName() { return taskName; }
    public boolean isCompleted() { return completed; }
}