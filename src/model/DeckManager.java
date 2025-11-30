package model;

import java.util.*;

public class DeckManager {
    private List<Deck> decks;
    
    public DeckManager() {
        this.decks = new ArrayList<>();
    }
    
    public void addDeck(Deck deck) {
        decks.add(deck);
    }
    
    public void removeDeck(Deck deck) {
        decks.remove(deck);
    }
    
    public List<Deck> getAllDecks() {
        return new ArrayList<>(decks);
    }
    
    public int getTotalCards() {
        return decks.stream()
            .mapToInt(Deck::getTotalCards)
            .sum();
    }
    
    public int getTotalCardsDue() {
        return decks.stream()
            .mapToInt(Deck::getCardsToReview)
            .sum();
    }
    
    public int getTotalMastered() {
        return decks.stream()
            .mapToInt(Deck::getMasteredCards)
            .sum();
    }
}