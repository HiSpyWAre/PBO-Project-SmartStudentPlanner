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
import database.*;
import java.util.List;

/**
 * StudyPlannerApp dengan Database Integration
 * 
 * PERUBAHAN UTAMA:
 * 1. Data sekarang disimpan di database SQLite
 * 2. Semua perubahan langsung di-save ke database
 * 3. Data persist antar sesi aplikasi
 */
public class StudyPlannerApp extends Application {

    // Database components
    private DatabaseManager dbManager;
    private UserDAO userDAO;
    private TaskDAO taskDAO;
    private FlashcardDAO flashcardDAO;
    private int currentUserId;
    
    private TaskManager taskManager;
    private UserProfile userProfile;
    private MainController controller;
    
    // UI components yang perlu di-update
    private Label userInfoLabel;
    private StackPane contentArea;
    private VBox sidebar;

    // FIXED start() method - Copy this into your StudyPlannerApp.java

@Override
public void start(Stage primaryStage) {
    // ===== 1. DATABASE INITIALIZATION =====
    System.out.println("🚀 Initializing database...");
    dbManager = DatabaseManager.getInstance();
    userDAO = new UserDAO(dbManager);
    taskDAO = new TaskDAO(dbManager);
    flashcardDAO = new FlashcardDAO(dbManager);
    
    // ===== 2. LOAD OR CREATE USER PROFILE =====
    System.out.println("👤 Loading user profile...");
    String username = "Student";
    userProfile = userDAO.loadUser(username);
    
    if (userProfile == null) {
        // First run - create new user
        System.out.println("🆕 First run detected - creating new user profile");
        userProfile = new UserProfile(username);
        currentUserId = userDAO.saveUser(userProfile);
    } else {
        // Existing user - get user ID
        System.out.println("✅ User profile loaded: Level " + userProfile.getLevel());
        currentUserId = getCurrentUserId(username);
    }
    
    // ===== 3. CREATE TASK MANAGER (BEFORE LOADING TASKS!) =====
    taskManager = new TaskManager();  // ✅ CREATE THIS FIRST!
    
    // ===== 4. LOAD TASKS FROM DATABASE =====
    System.out.println("📋 Loading tasks from database...");
    List<Task> loadedTasks = taskDAO.loadAllTasks(currentUserId);
    for (Task task : loadedTasks) {
        taskManager.addTask(task);
    }
    System.out.println("✅ Loaded " + loadedTasks.size() + " tasks");
    
    // ===== 5. LOAD SAMPLE DATA (ONLY IF EMPTY!) =====
    if (loadedTasks.isEmpty() && dbManager.isDatabaseEmpty()) {
        System.out.println("📝 Loading sample data...");
        loadSampleData();  // ✅ NOW taskManager exists!
    }
    
    // ===== 6. INITIALIZE CONTROLLER =====
    controller = new MainController(taskManager, userProfile);
    
    // ===== 7. SETUP AUTO-SAVE OBSERVERS =====
    setupAutoSaveObservers();

    // ===== 8. BUILD UI =====
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

    primaryStage.setTitle("Smart Study Planner (Database Edition)");
    primaryStage.setScene(scene);
    primaryStage.show();

    // Load stylesheet
    try {
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    } catch (Exception e) {
        System.out.println("Warning: Could not load styles.css - using default styling");
    }
    
    // ===== 9. SETUP CLOSE HANDLER =====
    primaryStage.setOnCloseRequest(e -> {
        System.out.println("💾 Saving final data before exit...");
        userDAO.updateUser(currentUserId, userProfile);
        dbManager.close();
        System.out.println("👋 Application closed successfully");
    });
    
    System.out.println("✅ Application started successfully!");
}
    /**
     * ⭐ NEW: Setup auto-save observers untuk database
     * Setiap perubahan langsung disimpan ke database
     */
    private void setupAutoSaveObservers() {
        // Profile observer - save user data when changed
        userProfile.addObserver(new ProfileObserver() {
            @Override
            public void onXPChanged(int newXP, int newLevel) {
                javafx.application.Platform.runLater(() -> {
                    updateUserInfoLabel();
                    refreshCurrentView();
                    // Auto-save to database
                    userDAO.updateUser(currentUserId, userProfile);
                });
            }
            
            @Override
            public void onStreakChanged(int newStreak) {
                javafx.application.Platform.runLater(() -> {
                    refreshCurrentView();
                    userDAO.updateUser(currentUserId, userProfile);
                });
            }
            
            @Override
            public void onAchievementUnlocked(Achievement achievement) {
                javafx.application.Platform.runLater(() -> {
                    showAchievementNotification(achievement);
                    refreshCurrentView();
                    userDAO.updateUser(currentUserId, userProfile);
                });
            }
            
            @Override
            public void onProductivityRecorded(int minutes) {
                javafx.application.Platform.runLater(() -> {
                    refreshCurrentView();
                    // Save productivity to database
                    userDAO.saveProductivity(currentUserId, java.time.LocalDate.now(), minutes);
                    userDAO.updateUser(currentUserId, userProfile);
                });
            }
        });
        
        // Task observer - save tasks when changed
        taskManager.addObserver(() -> {
            System.out.println("💾 Auto-saving tasks to database...");
            // This will be called when tasks are added/updated/deleted
            // The actual save happens in the UI methods (add/edit/delete)
        });
    }
    
    /**
     * Get user ID from database
     */
    private int getCurrentUserId(String username) {
        try {
            var conn = dbManager.getConnection();
            var stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                rs.close();
                stmt.close();
                return id;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Setup observer untuk mendengarkan perubahan pada UserProfile
     */
    private void setupProfileObserver() {
        userProfile.addObserver(new ProfileObserver() {
            @Override
            public void onXPChanged(int newXP, int newLevel) {
                javafx.application.Platform.runLater(() -> {
                    updateUserInfoLabel();
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
        
        javafx.scene.Node currentNode = contentArea.getChildren().get(0);
        
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
        dashPane.setUserData("dashboard");
        contentArea.getChildren().add(dashPane);
        updateSelectedButton(sidebar, (Button) sidebar.getChildren().get(0));
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 0 1 0;");

        Label title = new Label("📚 Smart Study Planner 💾");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        title.setTooltip(new Tooltip("Database Edition - All data is saved automatically"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        userInfoLabel = new Label("Level " + userProfile.getLevel() + " • " + userProfile.getXP() + " XP");
        userInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #a6adc8;");

        Button settingsBtn = new Button("⚙");
        settingsBtn.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-font-size: 18px;");
        settingsBtn.setOnAction(e -> showDatabaseInfo());

        header.getChildren().addAll(title, spacer, userInfoLabel, settingsBtn);
        return header;
    }
    
    /**
     * ⭐ NEW: Show database information dialog
     */
    private void showDatabaseInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Database Information");
        alert.setHeaderText("Smart Study Planner Database");
        
        String dbPath = dbManager.getDatabasePath();
        var stats = taskDAO.getTaskStatistics(currentUserId);
        
        String content = String.format("""
            📍 Database Location:
            %s
            
            📊 Statistics:
            • Total Tasks: %d
            • Completed: %d
            • In Progress: %d
            • Overdue: %d
            
            💡 Tip: Your data is automatically saved!
            """, 
            dbPath,
            stats.getOrDefault("total", 0),
            stats.getOrDefault("completed", 0),
            stats.getOrDefault("in_progress", 0),
            stats.getOrDefault("overdue", 0)
        );
        
        alert.setContentText(content);
        
        // Add backup button
        ButtonType backupBtn = new ButtonType("Backup Database");
        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(backupBtn, closeBtn);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == backupBtn) {
                // Create backup
                String backupPath = System.getProperty("user.home") + 
                    "/studyplanner_backup_" + System.currentTimeMillis() + ".db";
                dbManager.backupDatabase(backupPath);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Backup Complete");
                success.setContentText("Database backed up to:\n" + backupPath);
                success.showAndWait();
            }
        });
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

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
                dashboardBtn, tasksBtn, calendarBtn,
                pomodoroBtn, flashcardsBtn, spacer);

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

        dashboardBtn.setOnAction(e -> {
            switchToDashboard();
        });

        tasksBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            // Pass TaskDAO to TasksView for database operations
            contentArea.getChildren().add(
                new TasksViewWithDB(taskManager, controller, taskDAO, currentUserId).getView()
            );
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
    }

    private void updateSelectedButton(VBox sidebar, Button selectedBtn) {
        for (int i = 0; i < 5; i++) {
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
        System.out.println("📝 Loading sample data...");
        
        Task task1 = new Assignment("Metnum Problem Set", "Complete 2 & 3 stage",
                java.time.LocalDateTime.now().plusDays(3), 2, TaskPriority.HIGH);
        taskManager.addTask(task1);
        taskDAO.saveTask(currentUserId, task1);

        Task task2 = new Exam("Laprak Sistem Operasi", "laprak 10-12",
                java.time.LocalDateTime.now().plusDays(7), 5);
        taskManager.addTask(task2);
        taskDAO.saveTask(currentUserId, task2);

        Task task3 = new Project("PBO Project", "Build Java OOP application",
                java.time.LocalDateTime.now().plusDays(14), 10, TaskPriority.MEDIUM);
        taskManager.addTask(task3);
        taskDAO.saveTask(currentUserId, task3);
        
        System.out.println("✅ Sample data loaded and saved to database");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
