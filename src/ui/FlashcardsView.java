package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
// import model.*;
// import java.time.*;
// import java.time.format.DateTimeFormatter;
// import java.util.*;

public class FlashcardsView {
    private BorderPane view;
    
    public FlashcardsView() {
        this.view = new BorderPane();
        buildView();
    }
    
    private void buildView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(50));
        content.setAlignment(Pos.CENTER);
        
        Label title = new Label("🎴 Flashcards");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        Label comingSoon = new Label("Coming Soon!");
        comingSoon.setStyle("-fx-font-size: 24px; -fx-text-fill: #a6adc8;");
        
        Label description = new Label("Spaced repetition flashcard system with smart review scheduling");
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c7086; -fx-font-style: italic;");
        
        Button createDeckBtn = new Button("Create Your First Deck");
        createDeckBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; " +
                              "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 30;");
        
        content.getChildren().addAll(title, comingSoon, description, createDeckBtn);
        view.setCenter(content);
    }
    
    public BorderPane getView() {
        return view;

    }
}