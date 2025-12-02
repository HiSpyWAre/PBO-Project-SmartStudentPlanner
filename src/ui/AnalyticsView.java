package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import model.*;
// import java.time.*;
// import java.time.format.DateTimeFormatter;
import java.util.*;

public class AnalyticsView {
    private BorderPane view;
    private TaskManager taskManager;
    private UserProfile userProfile;
    
    // konstruktor untuk inisialisasi atribut
    public AnalyticsView(TaskManager taskManager, UserProfile userProfile) {
        this.taskManager = taskManager;
        this.userProfile = userProfile;
        this.view = new BorderPane();
        buildView();
    }
    
    // method untuk membangun tampilan analytics
    private void buildView() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2a2a3e; -fx-background-color: #2a2a3e;");
        
        // box utama
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        
        // judul dalam tampilan
        Label title = new Label("📈 Analytics & Insights");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        // Overview stats
        GridPane statsGrid = createStatsGrid();
        
        // Task completion chart
        VBox completionChart = createCompletionChart();
        
        // Priority distribution
        VBox priorityChart = createPriorityDistribution();
        
        // Study patterns
        VBox patternsCard = createStudyPatterns();
        
        content.getChildren().addAll(title, statsGrid, completionChart, priorityChart, patternsCard);
        scrollPane.setContent(content);
        view.setCenter(scrollPane);
    }
    
    // method untuk membuat grid statistik
    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        // cards untuk statistik utama
        VBox totalTasks = createAnalyticCard("Total Tasks", 
            String.valueOf(taskManager.getAllTasks().size()), "#89b4fa");

        // untuk tugas yang telah diselesaikan(completed)
        VBox completed = createAnalyticCard("Completed", 
            String.valueOf(taskManager.getTasksByStatus(TaskStatus.COMPLETED).size()), "#a6e3a1");
        
        // untuk tugas yang terlambat (overdue)
        VBox overdue = createAnalyticCard("Overdue", 
            String.valueOf(taskManager.getTasksByStatus(TaskStatus.OVERDUE).size()), "#f38ba8");
        
        // untuk rata-rata waktu penyelesaian tugas
        VBox avgTime = createAnalyticCard("Avg. Completion", "2.5 hrs", "#f9e2af");
        
        grid.add(totalTasks, 0, 0);
        grid.add(completed, 1, 0);
        grid.add(overdue, 2, 0);
        grid.add(avgTime, 3, 0);
        
        return grid;
    }
    
    // method untuk membuat card analitik individual
    private VBox createAnalyticCard(String label, String value, String color) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        // nilai utama
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        // label deskriptif
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 14px; -fx-text-fill: #a6adc8;");
        
        // menambahkan elemen ke dalam card
        card.getChildren().addAll(valueLabel, labelText);
        return card;
    }
    
    // method untuk membuat chart penyelesaian tugas
    private VBox createCompletionChart() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        // judul chart
        Label title = new Label("Task Completion Rate");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        int total = taskManager.getAllTasks().size();
        int completed = taskManager.getTasksByStatus(TaskStatus.COMPLETED).size();
        double percentage = total > 0 ? (double) completed / total * 100 : 0;
        
        // progress bar untuk menampilkan persentase penyelesaian
        ProgressBar progressBar = new ProgressBar(percentage / 100);
        progressBar.setPrefWidth(600);
        progressBar.setPrefHeight(30);
        progressBar.setStyle("-fx-accent: #a6e3a1;");
        
        Label percentLabel = new Label(String.format("%.1f%% Complete", percentage));
        percentLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #cdd6f4;");
        
        card.getChildren().addAll(title, progressBar, percentLabel);
        return card;
    }
    
    // method untuk membuat chart distribusi prioritas tugas
    private VBox createPriorityDistribution() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        Label title = new Label("Tasks by Priority");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        HBox bars = new HBox(20);
        bars.setAlignment(Pos.BOTTOM_CENTER);
        bars.setPrefHeight(200);
        
        Map<TaskPriority, Long> distribution = taskManager.getAllTasks().stream()
            .collect(java.util.stream.Collectors.groupingBy(Task::getPriority, java.util.stream.Collectors.counting()));
        
        for (TaskPriority priority : TaskPriority.values()) {
            long count = distribution.getOrDefault(priority, 0L);
            VBox bar = createPriorityBar(priority.toString(), (int)count);
            bars.getChildren().add(bar);
        }
        
        card.getChildren().addAll(title, bars);
        return card;
    }
    
    // method untuk membuat bar prioritas individual
    private VBox createPriorityBar(String label, int count) {
        VBox bar = new VBox(10);
        bar.setAlignment(Pos.BOTTOM_CENTER);
        
        Label countLabel = new Label(String.valueOf(count));
        countLabel.setStyle("-fx-text-fill: #cdd6f4; -fx-font-weight: bold;");
        
        Region barRegion = new Region();
        barRegion.setPrefWidth(80);
        barRegion.setPrefHeight(Math.max(20, count * 30));
        
        String color = switch (label) {
            case "URGENT" -> "#f38ba8";
            case "HIGH" -> "#fab387";
            case "MEDIUM" -> "#f9e2af";
            case "LOW" -> "#a6e3a1";
            default -> "#89b4fa";
        };
        barRegion.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5 5 0 0;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");
        
        bar.getChildren().addAll(countLabel, barRegion, labelText);
        return bar;
    }
    
    // method untuk membuat card pola belajar (study patterns)
    private VBox createStudyPatterns() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        Label title = new Label("📊 Study Patterns");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        Label insight1 = new Label("• Most productive time: Mornings (9-11 AM)");
        Label insight2 = new Label("• Average study session: 45 minutes");
        Label insight3 = new Label("• Best completion rate: Assignments (85%)");
        Label insight4 = new Label("• Current streak: " + userProfile.getStreak() + " days 🔥");
        
        insight1.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 14px;");
        insight2.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 14px;");
        insight3.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 14px;");
        insight4.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 14px;");
        
        card.getChildren().addAll(title, insight1, insight2, insight3, insight4);
        return card;
    }
    
    public BorderPane getView() {
        return view;
    }
}
