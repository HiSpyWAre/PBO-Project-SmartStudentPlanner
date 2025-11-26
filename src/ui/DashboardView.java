package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import model.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.*;

public class DashboardView {
    private BorderPane view;
    private TaskManager taskManager;
    private UserProfile userProfile;
    
    public DashboardView(TaskManager taskManager, UserProfile userProfile) {
        this.taskManager = taskManager;
        this.userProfile = userProfile;
        this.view = new BorderPane();
        buildView();
    }
    
    private void buildView() {
        // ketika scroll panel agar menyesuaikan user
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2a2a3e; -fx-background-color: #2a2a3e;");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        // Welcome section
        Label welcomeLabel = new Label("Welcome back, " + userProfile.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        // Stats cards row (box-box yg display dibawah welcome)
        HBox statsRow = createStatsRow();
        
        // Main content grid
        GridPane mainGrid = new GridPane();
        mainGrid.setHgap(20);
        mainGrid.setVgap(20);
        
        // Kolom kiri - Urgent tasks
        VBox urgentTasksCard = createUrgentTasksCard();
        GridPane.setHgrow(urgentTasksCard, Priority.ALWAYS);
        
        // Kolom kanan - Productivity chart
        VBox productivityCard = createProductivityCard();
        GridPane.setHgrow(productivityCard, Priority.ALWAYS);
        
        mainGrid.add(urgentTasksCard, 0, 0);
        mainGrid.add(productivityCard, 1, 0);
        
        // Achievements section
        VBox achievementsCard = createAchievementsCard();
        
        content.getChildren().addAll(welcomeLabel, statsRow, mainGrid, achievementsCard);
        scrollPane.setContent(content);
        view.setCenter(scrollPane);
    }

    private HBox createStatsRow() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        
        VBox streakCard = createStatCard("🔥 Streak", userProfile.getStreak() + " days", "#f38ba8");
        VBox levelCard = createStatCard("⭐ Level", String.valueOf(userProfile.getLevel()), "#89b4fa");
        VBox tasksCard = createStatCard("✓ Completed", 
            taskManager.getTasksByStatus(TaskStatus.COMPLETED).size() + " tasks", "#a6e3a1");
        VBox hoursCard = createStatCard("⏱ Study Time", 
            userProfile.getTotalStudyMinutes() / 60 + " hours", "#f9e2af");
        
        row.getChildren().addAll(streakCard, levelCard, tasksCard, hoursCard);
        return row;
    }
    
    // box yg besar untuk setiap statistik di dashboard
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #a6adc8;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    // box untuk menampilkan tugas-tugas paling mendesak
    private VBox createUrgentTasksCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        card.setPrefHeight(400);
        
        Label title = new Label("📌 Most Urgent Tasks");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        VBox tasksList = new VBox(10);
        List<Task> urgentTasks = taskManager.getTasksSortedByUrgency().stream()
            .limit(5)
            .toList();
        
        if (urgentTasks.isEmpty()) {
            // pesan ketika tidak ada tugas
            Label emptyLabel = new Label("No tasks yet! Add some to get started.");
            emptyLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-style: italic;");
            tasksList.getChildren().add(emptyLabel);
        } else {
            // menampilkan setiap tugas mendesak
            for (Task task : urgentTasks) {
                tasksList.getChildren().add(createTaskItem(task));
            }
        }
        
        card.getChildren().addAll(title, tasksList);
        return card;
    }
    
    // menampilkan item tugas individual dalam daftar tugas mendesak
    private HBox createTaskItem(Task task) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #45475a; -fx-background-radius: 8;");
        
        // Priority indicator
        String priorityColor = switch (task.getPriority()) {
            case URGENT -> "#f38ba8";
            case HIGH -> "#fab387";
            case MEDIUM -> "#f9e2af";
            case LOW -> "#a6e3a1";
        };
        
        Region priorityBar = new Region();
        priorityBar.setPrefWidth(4);
        priorityBar.setPrefHeight(50);
        priorityBar.setStyle("-fx-background-color: " + priorityColor + "; -fx-background-radius: 2;");
        
        // Task info
        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Label taskTitle = new Label(task.getTitle());
        taskTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        // Due date
        String dueText = "Due: " + task.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
        Label dueLabel = new Label(dueText);
        dueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #a6adc8;");
        
        info.getChildren().addAll(taskTitle, dueLabel);
        
        // Urgency score
        Label scoreLabel = new Label(String.format("%.0f", task.calculateUrgencyScore()));
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + priorityColor + ";");
        
        item.getChildren().addAll(priorityBar, info, scoreLabel);
        return item;
    }
    
    // box untuk menampilkan grafik produktivitas mingguan
    private VBox createProductivityCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        card.setPrefHeight(400);
        
        Label title = new Label("📊 This Week's Productivity");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        // Simple bar chart
        VBox chart = createWeeklyChart();
        
        card.getChildren().addAll(title, chart);
        return card;
    }

    // membuat grafik batang sederhana untuk produktivitas mingguan
    private VBox createWeeklyChart() {
        VBox chart = new VBox(10);
        chart.setAlignment(Pos.BOTTOM_CENTER);
        Map<LocalDate, Integer> weekData = userProfile.getLastWeekProductivity();
        
        int maxMinutes = weekData.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        
        HBox bars = new HBox(15);
        bars.setAlignment(Pos.BOTTOM_CENTER);
        bars.setPrefHeight(250);
        
        for (Map.Entry<LocalDate, Integer> entry : weekData.entrySet()) {
            VBox dayBar = new VBox(5);
            dayBar.setAlignment(Pos.BOTTOM_CENTER);
            
            int minutes = entry.getValue();
            double height = maxMinutes > 0 ? (double)minutes / maxMinutes * 200 : 0;
            
            Region bar = new Region();
            bar.setPrefWidth(40);
            bar.setPrefHeight(Math.max(5, height));
            bar.setStyle("-fx-background-color: #89b4fa; -fx-background-radius: 5 5 0 0;");
            
            String dayName = entry.getKey().format(DateTimeFormatter.ofPattern("EEE"));
            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #a6adc8;");
            
            Label valueLabel = new Label(minutes + "m");
            valueLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #cdd6f4;");
            
            dayBar.getChildren().addAll(valueLabel, bar, dayLabel);
            bars.getChildren().add(dayBar);
        }
        
        chart.getChildren().add(bars);
        return chart;
    }
    
    // box untuk menampilkan pencapaian terbaru pengguna
    private VBox createAchievementsCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        // Ikon dan judul
        Label title = new Label("🏆 Recent Achievements");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        // Barisan pencapaian
        HBox achievementsRow = new HBox(15);
        List<Achievement> unlocked = userProfile.getUnlockedAchievements();
        
        if (unlocked.isEmpty()) {
            // pesan ketika tidak ada pencapaian
            Label emptyLabel = new Label("Complete tasks to unlock achievements!");
            emptyLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-style: italic;");
            achievementsRow.getChildren().add(emptyLabel);
        } else {
            // menampilkan hingga maks 4 pencapaian terbaru
            for (Achievement achievement : unlocked.stream().limit(4).toList()) {
                VBox achCard = createAchievementBadge(achievement);
                achievementsRow.getChildren().add(achCard);
            }
        }
        
        card.getChildren().addAll(title, achievementsRow);
        return card;
    }
    
    // membuat badge pencapaian individual
    private VBox createAchievementBadge(Achievement achievement) {
        VBox badge = new VBox(10);
        badge.setAlignment(Pos.CENTER);
        badge.setPadding(new Insets(15));
        badge.setPrefWidth(150);
        badge.setStyle("-fx-background-color: #45475a; -fx-background-radius: 8;");
        
        // Ikon
        Label icon = new Label("🏆");
        icon.setStyle("-fx-font-size: 32px;");
        
        // Nama pencapaian
        Label name = new Label(achievement.getName());
        name.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #f9e2af; -fx-wrap-text: true;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(130);
        
        // XP reward
        Label xp = new Label("+" + achievement.getXpReward() + " XP");
        xp.setStyle("-fx-font-size: 10px; -fx-text-fill: #a6adc8;");
        
        // Menyusun elemen badge
        badge.getChildren().addAll(icon, name, xp);
        return badge;
    }
    
    // Getter untuk view utama
    public BorderPane getView() {
        return view;
    }
}