package ui;

import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.*;
// import model.*;
// import java.time.*;
// import java.time.format.DateTimeFormatter;
import java.util.*;

public class FlashcardsView {
    private BorderPane view;
    private model.DeckManager deckManager;
    private VBox mainContent;
    
    public FlashcardsView() {
        this.view = new BorderPane();
        this.deckManager = new model.DeckManager();
        loadSampleDecks(); // For demo
        buildView();
    }
    
    private void buildView() {
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        
        // Header
        HBox header = createHeader();
        
        // Stats row
        HBox stats = createStatsRow();
        
        // Decks grid
        GridPane decksGrid = createDecksGrid();
        
        mainContent.getChildren().addAll(header, stats, decksGrid);
        
        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #2a2a3e; -fx-background-color: #2a2a3e;");
        
        view.setCenter(scroll);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Flashcards");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addDeckBtn = new Button("+ New Deck");
        addDeckBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; " +
                           "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20;");
        addDeckBtn.setOnAction(e -> showCreateDeckDialog());
        
        header.getChildren().addAll(title, spacer, addDeckBtn);
        return header;
    }
    
    private HBox createStatsRow() {
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        
        VBox totalCards = createStatCard("Total Cards", 
            String.valueOf(deckManager.getTotalCards()), "#89b4fa");
        VBox dueCards = createStatCard("Due Today", 
            String.valueOf(deckManager.getTotalCardsDue()), "#f9e2af");
        VBox mastered = createStatCard("Mastered", 
            String.valueOf(deckManager.getTotalMastered()), "#a6e3a1");
        
        stats.getChildren().addAll(totalCards, dueCards, mastered);
        return stats;
    }
    
    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 14px; -fx-text-fill: #a6adc8;");
        
        card.getChildren().addAll(valueLabel, labelText);
        return card;
    }
    
    private GridPane createDecksGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        List<model.Deck> decks = deckManager.getAllDecks();
        
        int col = 0;
        int row = 0;
        
        for (model.Deck deck : decks) {
            VBox deckCard = createDeckCard(deck);
            grid.add(deckCard, col, row);
            
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
        
        return grid;
    }
    
    private VBox createDeckCard(model.Deck deck) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10; -fx-cursor: hand;");
        
        Label name = new Label(deck.getName());
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        Label description = new Label(deck.getDescription());
        description.setStyle("-fx-font-size: 12px; -fx-text-fill: #a6adc8;");
        description.setWrapText(true);
        
        HBox stats = new HBox(20);
        Label totalLabel = new Label(deck.getTotalCards() + " cards");
        totalLabel.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 12px;");
        
        Label dueLabel = new Label(deck.getCardsToReview() + " due");
        dueLabel.setStyle("-fx-text-fill: #f9e2af; -fx-font-size: 12px;");
        
        Label masteryLabel = new Label(String.format("%.0f%% mastery", deck.getAverageMastery()));
        masteryLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 12px;");
        
        stats.getChildren().addAll(totalLabel, dueLabel, masteryLabel);
        
        Button studyBtn = new Button("Study Now");
        studyBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; " +
                         "-fx-font-weight: bold; -fx-pref-width: 310;");
        studyBtn.setOnAction(e -> startStudySession(deck));
        
        Button manageBtn = new Button("Manage Cards");
        manageBtn.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-pref-width: 310;");
        manageBtn.setOnAction(e -> showManageCardsDialog(deck));
        
        card.getChildren().addAll(name, description, stats, studyBtn, manageBtn);
        
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #45475a; -fx-background-radius: 10; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10; -fx-cursor: hand;"));
        
        return card;
    }
    
    private void startStudySession(model.Deck deck) {
        List<model.Flashcard> dueCards = deck.getCardsDueForReview();
        
        if (dueCards.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Cards Due");
            alert.setHeaderText("Great job!");
            alert.setContentText("No cards are due for review in this deck. Come back later!");
            alert.showAndWait();
            return;
        }
        
        showStudyMode(deck, dueCards);
    }
    
    private void showStudyMode(model.Deck deck, List<model.Flashcard> cards) {
        Stage studyStage = new Stage();
        studyStage.setTitle("Study: " + deck.getName());
        
        BorderPane studyLayout = new BorderPane();
        studyLayout.setStyle("-fx-background-color: #1e1e2e;");
        
        VBox studyContent = new VBox(30);
        studyContent.setPadding(new Insets(50));
        studyContent.setAlignment(Pos.CENTER);
        studyContent.setPrefSize(800, 600);
        
        final int[] currentIndex = {0};
        final boolean[] showingAnswer = {false};
        
        Label progressLabel = new Label("Card " + (currentIndex[0] + 1) + " of " + cards.size());
        progressLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #a6adc8;");
        
        VBox cardBox = new VBox(30);
        cardBox.setPadding(new Insets(50));
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setStyle("-fx-background-color: #313244; -fx-background-radius: 15; -fx-min-height: 300;");
        
        Label questionLabel = new Label(cards.get(0).getQuestion());
        questionLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #cdd6f4; -fx-wrap-text: true;");
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(600);
        questionLabel.setAlignment(Pos.CENTER);
        
        Label answerLabel = new Label(cards.get(0).getAnswer());
        answerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #89b4fa; -fx-wrap-text: true;");
        answerLabel.setWrapText(true);
        answerLabel.setMaxWidth(600);
        answerLabel.setAlignment(Pos.CENTER);
        answerLabel.setVisible(false);
        
        cardBox.getChildren().addAll(questionLabel, answerLabel);
        
        Button showAnswerBtn = new Button("Show Answer");
        showAnswerBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; " +
                              "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 40;");
        
        HBox ratingButtons = new HBox(15);
        ratingButtons.setAlignment(Pos.CENTER);
        ratingButtons.setVisible(false);
        
        Button againBtn = createRatingButton("Again", "#f38ba8", 0);
        Button hardBtn = createRatingButton("Hard", "#fab387", 2);
        Button goodBtn = createRatingButton("Good", "#a6e3a1", 4);
        Button easyBtn = createRatingButton("Easy", "#89b4fa", 5);
        
        ratingButtons.getChildren().addAll(againBtn, hardBtn, goodBtn, easyBtn);
        
        showAnswerBtn.setOnAction(e -> {
            answerLabel.setVisible(true);
            showingAnswer[0] = true;
            showAnswerBtn.setVisible(false);
            ratingButtons.setVisible(true);
        });
        
        Runnable nextCard = () -> {
            currentIndex[0]++;
            if (currentIndex[0] < cards.size()) {
                model.Flashcard nextFlashcard = cards.get(currentIndex[0]);
                questionLabel.setText(nextFlashcard.getQuestion());
                answerLabel.setText(nextFlashcard.getAnswer());
                answerLabel.setVisible(false);
                showingAnswer[0] = false;
                showAnswerBtn.setVisible(true);
                ratingButtons.setVisible(false);
                progressLabel.setText("Card " + (currentIndex[0] + 1) + " of " + cards.size());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Session Complete!");
                alert.setHeaderText("Great work!");
                alert.setContentText("You've reviewed all " + cards.size() + " cards!");
                alert.showAndWait();
                studyStage.close();
                buildView(); // Refresh
            }
        };
        
        againBtn.setOnAction(e -> {
            cards.get(currentIndex[0]).recordReview(0);
            nextCard.run();
        });
        
        hardBtn.setOnAction(e -> {
            cards.get(currentIndex[0]).recordReview(2);
            nextCard.run();
        });
        
        goodBtn.setOnAction(e -> {
            cards.get(currentIndex[0]).recordReview(4);
            nextCard.run();
        });
        
        easyBtn.setOnAction(e -> {
            cards.get(currentIndex[0]).recordReview(5);
            nextCard.run();
        });
        
        studyContent.getChildren().addAll(progressLabel, cardBox, showAnswerBtn, ratingButtons);
        studyLayout.setCenter(studyContent);
        
        Scene scene = new Scene(studyLayout, 900, 700);
        studyStage.setScene(scene);
        studyStage.show();
    }
    
    private Button createRatingButton(String text, String color, int quality) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1e1e2e; " +
                    "-fx-font-weight: bold; -fx-padding: 10 20;");
        return btn;
    }
    
    private void showCreateDeckDialog() {
        Dialog<model.Deck> dialog = new Dialog<>();
        dialog.setTitle("Create New Deck");
        dialog.setHeaderText("Create a new flashcard deck");
        
        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Deck name");
        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn == createBtn && !nameField.getText().isEmpty()) {
                return new model.Deck(nameField.getText(), descField.getText());
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(deck -> {
            deckManager.addDeck(deck);
            buildView();
        });
    }
    
    private void showManageCardsDialog(model.Deck deck) {
        Stage stage = new Stage();
        stage.setTitle("Manage: " + deck.getName());
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        
        Button addCardBtn = new Button("+ Add Card");
        addCardBtn.setOnAction(e -> showAddCardDialog(deck, stage));
        
        ListView<String> cardsList = new ListView<>();
        cardsList.setPrefHeight(400);
        
        for (model.Flashcard card : deck.getAllCards()) {
            cardsList.getItems().add(card.getQuestion() + " -> " + card.getAnswer());
        }
        
        layout.getChildren().addAll(addCardBtn, cardsList);
        
        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }
    
    private void showAddCardDialog(model.Deck deck, Stage parentStage) {
        Dialog<model.Flashcard> dialog = new Dialog<>();
        dialog.setTitle("Add New Card");
        
        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextArea questionField = new TextArea();
        questionField.setPromptText("Question");
        questionField.setPrefRowCount(3);
        
        TextArea answerField = new TextArea();
        answerField.setPromptText("Answer");
        answerField.setPrefRowCount(3);
        
        TextField hintField = new TextField();
        hintField.setPromptText("Hint (optional)");
        
        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Answer:"), 0, 1);
        grid.add(answerField, 1, 1);
        grid.add(new Label("Hint:"), 0, 2);
        grid.add(hintField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn == addBtn && !questionField.getText().isEmpty() && !answerField.getText().isEmpty()) {
                return new model.Flashcard(questionField.getText(), answerField.getText(), hintField.getText());
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(card -> {
            deck.addCard(card);
            parentStage.close();
            showManageCardsDialog(deck);
        });
    }
    
    private void loadSampleDecks() {
        // Sample deck 1: Java Programming
        model.Deck javaDeck = new model.Deck("Java Programming", "Core Java concepts and syntax");
        javaDeck.addCard(new model.Flashcard("What is polymorphism?", "The ability of objects to take many forms. Allows methods to do different things based on the object."));
        javaDeck.addCard(new model.Flashcard("What is encapsulation?", "Bundling data and methods that operate on that data within a single unit (class)."));
        javaDeck.addCard(new model.Flashcard("What is inheritance?", "Mechanism where a new class inherits properties and methods from an existing class."));
        deckManager.addDeck(javaDeck);
        
        // Sample deck 2: Data Structures
        model.Deck dsDeck = new model.Deck("Data Structures", "Common data structures and algorithms");
        dsDeck.addCard(new model.Flashcard("What is Big O notation?", "Mathematical notation to describe algorithm complexity in terms of time or space."));
        dsDeck.addCard(new model.Flashcard("What is a Stack?", "LIFO (Last In First Out) data structure with push and pop operations."));
        dsDeck.addCard(new model.Flashcard("What is a Queue?", "FIFO (First In First Out) data structure with enqueue and dequeue operations."));
        deckManager.addDeck(dsDeck);
    }
    
    public BorderPane getView() {
        return view;
    }
}

// public class FlashcardsView {
//     private BorderPane view;
    
//     public FlashcardsView() {
//         this.view = new BorderPane();
//         buildView();
//     }
    
//     private void buildView() {
//         VBox content = new VBox(30);
//         content.setPadding(new Insets(50));
//         content.setAlignment(Pos.CENTER);
        
//         Label title = new Label("🎴 Flashcards");
//         title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
//         Label comingSoon = new Label("Coming Soon!");
//         comingSoon.setStyle("-fx-font-size: 24px; -fx-text-fill: #a6adc8;");
        
//         Label description = new Label("Spaced repetition flashcard system with smart review scheduling");
//         description.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c7086; -fx-font-style: italic;");
        
//         Button createDeckBtn = new Button("Create Your First Deck (SOON YA)");
//         createDeckBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; " +
//                               "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 30;");
        
//         content.getChildren().addAll(title, comingSoon, description, createDeckBtn);
//         view.setCenter(content);
//     }
    
//     public BorderPane getView() {
//         return view;

//     }
// }