// package smartStudyPlanner;
// Main Application class - entry point untuk aplikasi, mengatur window utama dan navigasi: initiasialisasi database, load data, setup UI utama, window closing 

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

    @Override
    public void start(Stage primaryStage) {
        // Inisialisasi data model
        taskManager = new TaskManager();
        userProfile = new UserProfile("Student");
        controller = new MainController(taskManager, userProfile);

        // Main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e2e;");

        // Top: Header isinta judul dan info pengguna
        HBox header = createHeader();
        root.setTop(header);

        // Left: Navigation sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Center: Main content area (will switch between views)
        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #2a2a3e;");
        root.setCenter(contentArea);

        // Initialize with dashboard view
        DashboardView dashboardView = new DashboardView(taskManager, userProfile);
        contentArea.getChildren().add(dashboardView.getView());

        // Setup navigation
        setupNavigation(sidebar, contentArea);

        // Create scene
        Scene scene = new Scene(root, 1000, 750);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000); // Minimum width
        primaryStage.setMinHeight(700); // Minimum height

        primaryStage.setTitle("Smart Study Planner");
        primaryStage.setScene(scene);
        primaryStage.show();

        // niatnya load stylesheet (css), tapi kalau gagal kasih warning di console
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: Could not load styles.css - using default styling");
        }
        // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Smart Study Planner");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load sample data untuk testing
        loadSampleData();
    }

    // method untuk membuat header - private untuk internal use saja
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 0 1 0;");

        Label title = new Label("📚 Smart Study Planner");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label("Level " + userProfile.getLevel() + " • " + userProfile.getXP() + " XP");
        userInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #a6adc8;");

        Button settingsBtn = new Button("⚙");
        settingsBtn.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-font-size: 18px;");

        header.getChildren().addAll(title, spacer, userInfo, settingsBtn);
        return header;
    }

    // method untuk membuat sidebar navigasi
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 1 0 0;");

        // true = tombol terpilih, false = tidak
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

    // method untuk membuat tombol navigasi
    private Button createNavButton(String text, boolean selected) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(45);
        btn.setAlignment(Pos.CENTER_LEFT);

        // Styling berdasarkan status terpilih atau tidak
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
        // Get buttons (tombol) dari sidebar
        Button dashboardBtn = (Button) sidebar.getChildren().get(0);
        Button tasksBtn = (Button) sidebar.getChildren().get(1);
        Button calendarBtn = (Button) sidebar.getChildren().get(2);
        Button pomodoroBtn = (Button) sidebar.getChildren().get(3);
        Button flashcardsBtn = (Button) sidebar.getChildren().get(4);
        Button analyticsBtn = (Button) sidebar.getChildren().get(5);

        // Setup click handlers = mengarahkan ke view yang sesuai
        dashboardBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new DashboardView(taskManager, userProfile).getView());
            updateSelectedButton(sidebar, dashboardBtn);
        });

        tasksBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new TasksView(taskManager).getView());
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

    // method untuk mengupdate styling tombol terpilih di sidebar
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

    // method untuk load sample data
    private void loadSampleData() {
        // Add some sample tasks
        taskManager.addTask(new Assignment("Metnum Problem Set", "Complete 2 & 3 stage",
                java.time.LocalDateTime.now().plusDays(3), 2, TaskPriority.HIGH));

        taskManager.addTask(new Exam("Laprak Sistem Operasi", "laprak 10-12",
                java.time.LocalDateTime.now().plusDays(7), 5));

        taskManager.addTask(new Project("PBO Project", "Build Java OOP application",
                java.time.LocalDateTime.now().plusDays(14), 10, TaskPriority.MEDIUM));
    }

    // main method
    public static void main(String[] args) {
        launch(args);
    }
}
