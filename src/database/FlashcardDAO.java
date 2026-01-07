package database;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * FlashcardDAO - Data Access Object untuk operasi database Flashcards
 */
public class FlashcardDAO {
    private DatabaseManager dbManager;
    
    public FlashcardDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    /**
     * Save deck to database
     */
    public int saveDeck(int userId, Deck deck) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                INSERT INTO decks (user_id, name, description, category, created_date)
                VALUES (?, ?, ?, ?, ?)
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.setString(2, deck.getName());
            stmt.setString(3, deck.getDescription());
            stmt.setString(4, deck.getCategory());
            stmt.setString(5, deck.getCreatedDate().toString());
            
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int deckId = generatedKeys.getInt(1);
            
            generatedKeys.close();
            stmt.close();
            
            // Save all cards in this deck
            for (Flashcard card : deck.getAllCards()) {
                saveFlashcard(deckId, card);
            }
            
            System.out.println("✅ Deck saved: " + deck.getName() + " (ID: " + deckId + ")");
            return deckId;
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to save deck!");
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Update deck
     */
    public void updateDeck(int deckId, Deck deck) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                UPDATE decks 
                SET name = ?, description = ?, category = ?
                WHERE id = ?
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, deck.getName());
            stmt.setString(2, deck.getDescription());
            stmt.setString(3, deck.getCategory());
            stmt.setInt(4, deckId);
            
            stmt.executeUpdate();
            stmt.close();
            
            System.out.println("✅ Deck updated: " + deck.getName());
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to update deck!");
            e.printStackTrace();
        }
    }
    
    /**
     * Delete deck
     */
    public void deleteDeck(int deckId) {
        try {
            Connection conn = dbManager.getConnection();
            
            // First delete all flashcards in this deck
            String deleteCardsSql = "DELETE FROM flashcards WHERE deck_id = ?";
            PreparedStatement deleteCardsStmt = conn.prepareStatement(deleteCardsSql);
            deleteCardsStmt.setInt(1, deckId);
            deleteCardsStmt.executeUpdate();
            deleteCardsStmt.close();
            
            // Then delete the deck
            String sql = "DELETE FROM decks WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deckId);
            stmt.executeUpdate();
            stmt.close();
            
            System.out.println("✅ Deck deleted (ID: " + deckId + ")");
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete deck!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load all decks for a user
     */
    public List<Deck> loadAllDecks(int userId) {
        List<Deck> decks = new ArrayList<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT * FROM decks WHERE user_id = ? ORDER BY created_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int deckId = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String category = rs.getString("category");
                
                Deck deck = new Deck(name, description);
                deck.setCategory(category);
                
                // Load all flashcards for this deck
                List<Flashcard> cards = loadFlashcardsByDeck(deckId);
                for (Flashcard card : cards) {
                    deck.addCard(card);
                }
                
                decks.add(deck);
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("✅ Loaded " + decks.size() + " decks");
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to load decks!");
            e.printStackTrace();
        }
        
        return decks;
    }
    
    /**
     * Save flashcard to database
     */
    public int saveFlashcard(int deckId, Flashcard card) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                INSERT INTO flashcards (deck_id, question, answer, ease_factor, repetitions, 
                                       interval, next_review, created_date, last_reviewed, 
                                       total_reviews, correct_count)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, deckId);
            stmt.setString(2, card.getQuestion());
            stmt.setString(3, card.getAnswer());
            stmt.setInt(4, 2500); // Default ease factor
            stmt.setInt(5, card.getRepetitions());
            stmt.setInt(6, card.getInterval());
            stmt.setString(7, card.getNextReview().toString());
            stmt.setString(8, card.getCreatedDate().toString());
            stmt.setString(9, card.getLastReviewed() != null ? card.getLastReviewed().toString() : null);
            stmt.setInt(10, card.getTotalReviews());
            stmt.setInt(11, card.getCorrectCount());
            
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int cardId = generatedKeys.getInt(1);
            
            generatedKeys.close();
            stmt.close();
            
            return cardId;
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to save flashcard!");
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Update flashcard
     */
    public void updateFlashcard(int cardId, Flashcard card) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                UPDATE flashcards 
                SET question = ?, answer = ?, ease_factor = ?, repetitions = ?, 
                    interval = ?, next_review = ?, last_reviewed = ?, 
                    total_reviews = ?, correct_count = ?
                WHERE id = ?
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, card.getQuestion());
            stmt.setString(2, card.getAnswer());
            stmt.setInt(3, 2500); // Ease factor
            stmt.setInt(4, card.getRepetitions());
            stmt.setInt(5, card.getInterval());
            stmt.setString(6, card.getNextReview().toString());
            stmt.setString(7, card.getLastReviewed() != null ? card.getLastReviewed().toString() : null);
            stmt.setInt(8, card.getTotalReviews());
            stmt.setInt(9, card.getCorrectCount());
            stmt.setInt(10, cardId);
            
            stmt.executeUpdate();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to update flashcard!");
            e.printStackTrace();
        }
    }
    
    /**
     * Delete flashcard
     */
    public void deleteFlashcard(int cardId) {
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "DELETE FROM flashcards WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cardId);
            
            stmt.executeUpdate();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete flashcard!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load all flashcards for a deck
     */
    public List<Flashcard> loadFlashcardsByDeck(int deckId) {
        List<Flashcard> cards = new ArrayList<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = "SELECT * FROM flashcards WHERE deck_id = ? ORDER BY created_date ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deckId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                int repetitions = rs.getInt("repetitions");
                int interval = rs.getInt("interval");
                LocalDateTime nextReview = LocalDateTime.parse(rs.getString("next_review"));
                String lastReviewedStr = rs.getString("last_reviewed");
                int totalReviews = rs.getInt("total_reviews");
                int correctCount = rs.getInt("correct_count");
                
                Flashcard card = new Flashcard(question, answer);
                
                // Restore review state by simulating reviews
                for (int i = 0; i < totalReviews; i++) {
                    // This is a simplified restoration
                    // In a real scenario, you'd need to store more detailed history
                    if (i < correctCount) {
                        card.recordReview(4); // Good
                    } else {
                        card.recordReview(0); // Again
                    }
                }
                
                cards.add(card);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to load flashcards!");
            e.printStackTrace();
        }
        
        return cards;
    }
    
    /**
     * Get cards due for review
     */
    public List<Flashcard> getCardsDueForReview(int deckId) {
        List<Flashcard> dueCards = new ArrayList<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            String sql = """
                SELECT * FROM flashcards 
                WHERE deck_id = ? AND next_review <= ?
                ORDER BY next_review ASC
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deckId);
            stmt.setString(2, LocalDateTime.now().toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                
                Flashcard card = new Flashcard(question, answer);
                dueCards.add(card);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to get due cards!");
            e.printStackTrace();
        }
        
        return dueCards;
    }
    
    /**
     * Get flashcard statistics for a deck
     */
    public Map<String, Integer> getDeckStatistics(int deckId) {
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            Connection conn = dbManager.getConnection();
            
            // Total cards
            String totalSql = "SELECT COUNT(*) as count FROM flashcards WHERE deck_id = ?";
            PreparedStatement totalStmt = conn.prepareStatement(totalSql);
            totalStmt.setInt(1, deckId);
            ResultSet totalRs = totalStmt.executeQuery();
            stats.put("total", totalRs.getInt("count"));
            totalRs.close();
            totalStmt.close();
            
            // Cards due for review
            String dueSql = """
                SELECT COUNT(*) as count FROM flashcards 
                WHERE deck_id = ? AND next_review <= ?
            """;
            PreparedStatement dueStmt = conn.prepareStatement(dueSql);
            dueStmt.setInt(1, deckId);
            dueStmt.setString(2, LocalDateTime.now().toString());
            ResultSet dueRs = dueStmt.executeQuery();
            stats.put("due", dueRs.getInt("count"));
            dueRs.close();
            dueStmt.close();
            
            // Mastered cards (repetitions >= 5)
            String masteredSql = "SELECT COUNT(*) as count FROM flashcards WHERE deck_id = ? AND repetitions >= 5";
            PreparedStatement masteredStmt = conn.prepareStatement(masteredSql);
            masteredStmt.setInt(1, deckId);
            ResultSet masteredRs = masteredStmt.executeQuery();
            stats.put("mastered", masteredRs.getInt("count"));
            masteredRs.close();
            masteredStmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to get deck statistics!");
            e.printStackTrace();
        }
        
        return stats;
    }
}