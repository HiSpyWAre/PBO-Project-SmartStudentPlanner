import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import ui.*;
import model.*;
import controller.*;

public class StudyPlannerApp extends Application {

    private TaskManager taskManager;
    private UserProfile userProfile;
    private MainController controller;
    
    // UI components yang perlu di-update
    private Label userInfoLabel;
    private StackPane contentArea;
    private VBox sidebar;

    @Override
    public void start(Stage primaryStage) {
        // Inisialisasi data model
        taskManager = new TaskManager();
        userProfile = new UserProfile("Student");
        controller = new MainController(taskManager, userProfile);

        // Main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e2e;");

        // Top: Header dengan info pengguna
        HBox header = createHeader();
        root.setTop(header);

        // Left: Navigation sidebar
        sidebar = createSidebar();
        root.setLeft(sidebar);

        // Center: Main content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #2a2a3e;");
        root.setCenter(contentArea);

        // Initialize with dashboard view
        switchToDashboard();

        // Setup navigation
        setupNavigation(sidebar, contentArea);

        // Create scene
        Scene scene = new Scene(root, 1000, 750);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        primaryStage.setTitle("Smart Study Planner");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load stylesheet
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: Could not load styles.css - using default styling");
        }

        // Load sample data
        loadSampleData();
        
        // ⭐ REGISTER OBSERVER untuk real-time updates
        setupProfileObserver();
    }
    
    /**
     * Setup observer untuk mendengarkan perubahan pada UserProfile
     */
    private void setupProfileObserver() {
        userProfile.addObserver(new ProfileObserver() {
            @Override
            public void onXPChanged(int newXP, int newLevel) {
                // Update header info secara real-time
                javafx.application.Platform.runLater(() -> {
                    updateUserInfoLabel();
                    // Refresh current view jika Dashboard
                    refreshCurrentView();
                });
            }
            
            @Override
            public void onStreakChanged(int newStreak) {
                javafx.application.Platform.runLater(() -> {
                    refreshCurrentView();
                });
            }
            
            @Override
            public void onAchievementUnlocked(Achievement achievement) {
                javafx.application.Platform.runLater(() -> {
                    showAchievementNotification(achievement);
                    refreshCurrentView();
                });
            }
            
            @Override
            public void onProductivityRecorded(int minutes) {
                javafx.application.Platform.runLater(() -> {
                    refreshCurrentView();
                });
            }
        });
    }
    
    /**
     * Update label info user di header
     */
    private void updateUserInfoLabel() {
        if (userInfoLabel != null) {
            userInfoLabel.setText("Level " + userProfile.getLevel() + " • " + userProfile.getXP() + " XP");
        }
    }
    
    /**
     * Refresh view yang sedang aktif
     */
    private void refreshCurrentView() {
        if (contentArea.getChildren().isEmpty()) return;
        
        // Detect current view dan refresh
        javafx.scene.Node currentNode = contentArea.getChildren().get(0);
        
        // Jika Dashboard, refresh dashboard
        if (currentNode.getUserData() != null && currentNode.getUserData().equals("dashboard")) {
            switchToDashboard();
        }
    }
    
    /**
     * Show notification ketika achievement unlocked
     */
    private void showAchievementNotification(Achievement achievement) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🏆 Achievement Unlocked!");
        alert.setHeaderText(achievement.getName());
        alert.setContentText(achievement.getDescription() + "\n\n+" + achievement.getXpReward() + " XP!");
        
        // Custom styling untuk alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1e1e2e;");
        
        alert.showAndWait();
    }

    /**
     * Switch ke Dashboard view
     */
    private void switchToDashboard() {
        contentArea.getChildren().clear();
        DashboardView dashboardView = new DashboardView(taskManager, userProfile);
        BorderPane dashPane = dashboardView.getView();
        dashPane.setUserData("dashboard"); // Mark sebagai dashboard
        contentArea.getChildren().add(dashPane);
        updateSelectedButton(sidebar, (Button) sidebar.getChildren().get(0));
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 0 1 0;");

        Label title = new Label("📚 Smart Study Planner");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ⭐ Simpan reference ke userInfoLabel
        userInfoLabel = new Label("Level " + userProfile.getLevel() + " • " + userProfile.getXP() + " XP");
        userInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #a6adc8;");

        Button settingsBtn = new Button("⚙");
        settingsBtn.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-font-size: 18px;");

        header.getChildren().addAll(title, spacer, userInfoLabel, settingsBtn);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 1 0 0;");

        Button dashboardBtn = createNavButton("📊 Dashboard", true);
        Button tasksBtn = createNavButton("✓ Tasks", false);
        Button calendarBtn = createNavButton("📅 Calendar", false);
        Button pomodoroBtn = createNavButton("⏱ Pomodoro", false);
        Button flashcardsBtn = createNavButton("🎴 Flashcards", false);
        Button analyticsBtn = createNavButton("📈 Analytics", false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
                dashboardBtn, tasksBtn, calendarBtn,
                pomodoroBtn, flashcardsBtn, analyticsBtn, spacer);

        return sidebar;
    }

    private Button createNavButton(String text, boolean selected) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(45);
        btn.setAlignment(Pos.CENTER_LEFT);

        if (selected) {
            btn.setStyle(
                    "-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 14px;");
            btn.setOnMouseEntered(
                    e -> btn.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-font-size: 14px;"));
            btn.setOnMouseExited(e -> btn
                    .setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 14px;"));
        }

        return btn;
    }

    private void setupNavigation(VBox sidebar, StackPane contentArea) {
        Button dashboardBtn = (Button) sidebar.getChildren().get(0);
        Button tasksBtn = (Button) sidebar.getChildren().get(1);
        Button calendarBtn = (Button) sidebar.getChildren().get(2);
        Button pomodoroBtn = (Button) sidebar.getChildren().get(3);
        Button flashcardsBtn = (Button) sidebar.getChildren().get(4);
        Button analyticsBtn = (Button) sidebar.getChildren().get(5);

        dashboardBtn.setOnAction(e -> {
            switchToDashboard();
        });

        tasksBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new TasksView(taskManager, controller).getView());
            updateSelectedButton(sidebar, tasksBtn);
        });

        calendarBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new CalendarView(taskManager).getView());
            updateSelectedButton(sidebar, calendarBtn);
        });

        pomodoroBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new PomodoroView(userProfile).getView());
            updateSelectedButton(sidebar, pomodoroBtn);
        });

        flashcardsBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new FlashcardsView().getView());
            updateSelectedButton(sidebar, flashcardsBtn);
        });

        analyticsBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new AnalyticsView(taskManager, userProfile).getView());
            updateSelectedButton(sidebar, analyticsBtn);
        });
    }

    private void updateSelectedButton(VBox sidebar, Button selectedBtn) {
        for (int i = 0; i < 6; i++) {
            Button btn = (Button) sidebar.getChildren().get(i);
            if (btn == selectedBtn) {
                btn.setStyle(
                        "-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 14px;");
            }
        }
    }

    private void loadSampleData() {
        taskManager.addTask(new Assignment("Metnum Problem Set", "Complete 2 & 3 stage",
                java.time.LocalDateTime.now().plusDays(3), 2, TaskPriority.HIGH));

        taskManager.addTask(new Exam("Laprak Sistem Operasi", "laprak 10-12",
                java.time.LocalDateTime.now().plusDays(7), 5));

        taskManager.addTask(new Project("PBO Project", "Build Java OOP application",
                java.time.LocalDateTime.now().plusDays(14), 10, TaskPriority.MEDIUM));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

