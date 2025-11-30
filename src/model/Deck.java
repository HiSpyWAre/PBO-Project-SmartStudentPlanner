package model;

import java.time.LocalDateTime;
import java.util.*;

public class Deck {  // ← Tambahkan "public" di sini!
    private int id;
    private String name;
    private String description;
    private List<Flashcard> cards;
    private LocalDateTime createdDate;
    private String category;
    
    private static int idCounter = 0;
    
    public Deck(String name, String description) {
        this.id = ++idCounter;
        this.name = name;
        this.description = description;
        this.cards = new ArrayList<>();
        this.createdDate = LocalDateTime.now();
        this.category = "General";
    }
    
    public void addCard(Flashcard card) {
        cards.add(card);
    }
    
    public void removeCard(Flashcard card) {
        cards.remove(card);
    }
    
    public List<Flashcard> getCardsDueForReview() {
        return cards.stream()
            .filter(Flashcard::isDueForReview)
            .toList();
    }
    
    public List<Flashcard> getAllCards() {
        return new ArrayList<>(cards);
    }
    
    public int getTotalCards() {
        return cards.size();
    }
    
    public int getCardsToReview() {
        return (int) cards.stream()
            .filter(Flashcard::isDueForReview)
            .count();
    }
    
    public int getMasteredCards() {
        return (int) cards.stream()
            .filter(c -> c.getDifficultyLevel().equals("Mastered"))
            .count();
    }
    
    public double getAverageMastery() {
        if (cards.isEmpty()) return 0.0;
        return cards.stream()
            .mapToDouble(Flashcard::getMasteryPercentage)
            .average()
            .orElse(0.0);
    }
    
    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
