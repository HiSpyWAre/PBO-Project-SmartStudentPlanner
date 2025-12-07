package model;

import java.time.LocalDateTime;

public class Flashcard {
    private int id;
    private String question;
    private String answer;
    private String hint;
    private int easeFactor; // 0-5 (how easy the card is)
    private int repetitions; // number of successful reviews
    private int interval; // days until next review
    private LocalDateTime nextReview;
    private LocalDateTime createdDate;
    private LocalDateTime lastReviewed;
    private int totalReviews;
    private int correctCount;
    
    private static int idCounter = 0;
    
    public Flashcard(String question, String answer) {
        this.id = ++idCounter;
        this.question = question;
        this.answer = answer;
        this.hint = "";
        this.easeFactor = 2500; // Default ease factor (2.5 * 1000)
        this.repetitions = 0;
        this.interval = 0;
        this.nextReview = LocalDateTime.now();
        this.createdDate = LocalDateTime.now();
        this.totalReviews = 0;
        this.correctCount = 0;
    }
    
    public Flashcard(String question, String answer, String hint) {
        this(question, answer);
        this.hint = hint;
    }
    
    // SM-2 Spaced Repetition Algorithm
    public void recordReview(int quality) {
        // quality: 0-5 where 5=perfect, 0=total blackout
        totalReviews++;
        
        if (quality >= 3) {
            correctCount++;
            
            if (repetitions == 0) {
                interval = 1;
            } else if (repetitions == 1) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * (easeFactor / 1000.0));
            }
            
            repetitions++;
        } else {
            repetitions = 0;
            interval = 1;
        }
        
        // Update ease factor
        easeFactor = easeFactor + (850 - (350 - quality * 75));
        if (easeFactor < 1300) easeFactor = 1300;
        
        // Schedule next review
        nextReview = LocalDateTime.now().plusDays(interval);
        lastReviewed = LocalDateTime.now();
    }
    
    public boolean isDueForReview() {
        return LocalDateTime.now().isAfter(nextReview);
    }
    
    public double getMasteryPercentage() {
        if (totalReviews == 0) return 0.0;
        return (double) correctCount / totalReviews * 100.0;
    }
    
    public String getDifficultyLevel() {
        if (repetitions >= 5) return "Mastered";
        if (repetitions >= 3) return "Learning";
        if (repetitions >= 1) return "Familiar";
        return "New";
    }
    
    // Getters and setters
    public int getId() { 
        return id; 
    }
    public String getQuestion() { 
        return question; 
    }
    public void setQuestion(String question) { 
        this.question = question; 
    }
    public String getAnswer() { 
        return answer; 
    }
    public void setAnswer(String answer) { 
        this.answer = answer; 
    }
    public String getHint() { 
        return hint; 
    }
    public void setHint(String hint) { 
        this.hint = hint; 
    }
    public int getRepetitions() { 
        return repetitions; 
    }
    public int getInterval() { 
        return interval; 
    }
    public LocalDateTime getNextReview() { 
        return nextReview; 
    }
    public LocalDateTime getCreatedDate() { 
        return createdDate; 
    }
    public LocalDateTime getLastReviewed() { 
        return lastReviewed; 
    }
    public int getTotalReviews() { 
        return totalReviews; 
    }

    public int getCorrectCount() { 
        return correctCount; 
    }
}
