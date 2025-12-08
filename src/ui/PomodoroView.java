package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import model.*;

/**
 * PomodoroView dengan timer persisten dan UI yang update real-time
 */
public class PomodoroView {
    private BorderPane view;
    private UserProfile userProfile;

    // STATIC - current active instance (untuk update UI)
    private static PomodoroView currentInstance;

    // Instance variables untuk UI
    private Label timerLabel;
    private Button startPauseBtn;
    private Circle progressCircle;
    private Arc progressArc;
    private Label modeLabel;

    // STATIC variables - shared state
    private static Timeline timeline;
    private static int timeRemaining;
    private static int sessionDuration = 25 * 60;
    private static int breakDuration = 5 * 60;
    private static boolean isRunning = false;
    private static boolean isBreak = false;
    private static int completedSessions = 0;

    public PomodoroView(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.view = new BorderPane();

        // Set sebagai current instance
        currentInstance = this;

        // Initialize timeRemaining hanya sekali
        if (timeRemaining == 0 && completedSessions == 0) {
            timeRemaining = sessionDuration;
        }

        buildView();
    }

    private void buildView() {
        VBox content = new VBox(40);
        content.setPadding(new Insets(50));
        content.setAlignment(Pos.CENTER);

        Label title = new Label("🍅 Pomodoro Timer");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");

        // stack pane untuk timer display
        StackPane timerDisplay = createTimerDisplay();
        HBox controls = createControls();
        HBox settings = createSettings();
        VBox sessionInfo = createSessionInfo();
        HBox stats = createStatistics();

        content.getChildren().addAll(title, timerDisplay, controls, settings, sessionInfo, stats);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2a2a3e; -fx-background-color: #2a2a3e;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        view.setCenter(scrollPane);

        // Update UI dengan state saat ini
        updateTimerDisplay();
        updateButtonState();
    }

    private StackPane createTimerDisplay() {
        StackPane stack = new StackPane();

        progressCircle = new Circle(150);
        progressCircle.setFill(Color.TRANSPARENT);
        progressCircle.setStroke(Color.web("#313244"));
        progressCircle.setStrokeWidth(15);

        progressArc = new Arc();
        progressArc.setCenterX(0);
        progressArc.setCenterY(0);
        progressArc.setRadiusX(150);
        progressArc.setRadiusY(150);
        progressArc.setStartAngle(90);
        progressArc.setLength(0);
        progressArc.setType(ArcType.OPEN);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.setStroke(Color.web("#89b4fa"));
        progressArc.setStrokeWidth(15);
        progressArc.setStrokeLineCap(StrokeLineCap.ROUND);

        timerLabel = new Label(formatTime(timeRemaining));
        timerLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");

        modeLabel = new Label(isBreak ? "Break Time" : "Focus Time");
        modeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #a6adc8;");

        VBox timerBox = new VBox(10, timerLabel, modeLabel);
        timerBox.setAlignment(Pos.CENTER);

        stack.getChildren().addAll(progressCircle, progressArc, timerBox);
        return stack;
    }

    private HBox createControls() {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        // conditional berdasarkan state
        startPauseBtn = new Button(isRunning ? "Pause"
                : (timeRemaining == sessionDuration || timeRemaining == breakDuration ? "Start" : "Resume"));
        startPauseBtn.setPrefSize(150, 50);
        startPauseBtn.setOnAction(e -> toggleTimer());

        Button resetBtn = new Button("Reset");
        resetBtn.setPrefSize(150, 50);
        resetBtn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: #1e1e2e; " +
                "-fx-font-size: 18px; -fx-font-weight: bold;");
        resetBtn.setOnAction(e -> resetTimer());

        Button skipBtn = new Button("Skip");
        skipBtn.setPrefSize(150, 50);
        skipBtn.setStyle("-fx-background-color: #fab387; -fx-text-fill: #1e1e2e; " +
                "-fx-font-size: 18px; -fx-font-weight: bold;");
        skipBtn.setOnAction(e -> skipSession());

        controls.getChildren().addAll(startPauseBtn, resetBtn, skipBtn);
        return controls;
    }

    // method untuk membuat pengaturan durasi
    private HBox createSettings() {
        HBox settings = new HBox(30);
        settings.setAlignment(Pos.CENTER);
        settings.setPadding(new Insets(20));
        settings.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");

        VBox focusSettings = new VBox(10);
        focusSettings.setAlignment(Pos.CENTER);
        Label focusLabel = new Label("Focus Duration");
        focusLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");

        Spinner<Integer> focusSpinner = new Spinner<>(15, 60, sessionDuration / 60, 5);
        focusSpinner.setPrefWidth(100);
        focusSpinner.valueProperty().addListener((obs, old, newVal) -> {
            sessionDuration = newVal * 60;
            if (!isRunning && !isBreak) {
                timeRemaining = sessionDuration;
                updateTimerDisplay();
            }
        });

        focusSettings.getChildren().addAll(focusLabel, focusSpinner);

        VBox breakSettings = new VBox(10);
        breakSettings.setAlignment(Pos.CENTER);
        Label breakLabel = new Label("Break Duration");
        breakLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");

        // untuk break duration
        Spinner<Integer> breakSpinner = new Spinner<>(3, 15, breakDuration / 60, 1);
        breakSpinner.setPrefWidth(100);
        breakSpinner.valueProperty().addListener((obs, old, newVal) -> {
            breakDuration = newVal * 60;
        });

        breakSettings.getChildren().addAll(breakLabel, breakSpinner);

        settings.getChildren().addAll(focusSettings, breakSettings);
        return settings;
    }

    // method untuk membuat info sesi
    private VBox createSessionInfo() {
        VBox info = new VBox(10);
        info.setAlignment(Pos.CENTER);

        Label sessionsLabel = new Label("Completed Sessions Today");
        sessionsLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 14px;");

        Label sessionsCount = new Label(String.valueOf(completedSessions));
        sessionsCount.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 48px; -fx-font-weight: bold;");

        HBox dots = new HBox(10);
        dots.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            Circle dot = new Circle(8);
            dot.setFill(i < completedSessions % 4 ? Color.web("#f38ba8") : Color.web("#313244"));
            dots.getChildren().add(dot);
        }

        Label longBreakLabel = new Label("Long break after 4 sessions");
        longBreakLabel.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 11px; -fx-font-style: italic;");

        info.getChildren().addAll(sessionsLabel, sessionsCount, dots, longBreakLabel);
        return info;
    }

    // method untuk membuat statistik
    private HBox createStatistics() {
        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);

        VBox totalSessions = createStatBox("Total Sessions",
                String.valueOf(userProfile.getTotalPomodoroSessions()), "#89b4fa");
        VBox totalTime = createStatBox("Total Focus Time",
                userProfile.getTotalStudyMinutes() / 60 + "h", "#a6e3a1");
        VBox todayTime = createStatBox("Today",
                "0h 0m", "#f9e2af");

        stats.getChildren().addAll(totalSessions, totalTime, todayTime);
        return stats;
    }

    // method untuk membuat box statistik individual
    private VBox createStatBox(String label, String value, String color) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setPrefWidth(200);
        box.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");

        Label labelText = new Label(label);
        labelText.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");

        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 32px; -fx-font-weight: bold;");

        box.getChildren().addAll(labelText, valueText);
        return box;
    }
    
    // toggle timer start/pause
    private void toggleTimer() {
        if (isRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        isRunning = true;
        updateButtonState();

        // Recreate timeline jika belum ada atau sudah stopped
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeRemaining--;

                // Update UI dari current instance (yang sedang aktif)
                if (currentInstance != null) {
                    javafx.application.Platform.runLater(() -> {
                        currentInstance.updateTimerDisplay();
                    });
                }

                if (timeRemaining <= 0) {
                    sessionComplete();
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }

        timeline.play();
    }

    private void pauseTimer() {
        isRunning = false;
        updateButtonState();

        if (timeline != null) {
            timeline.pause();
        }
    }

    private void resetTimer() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        isRunning = false;
        timeRemaining = isBreak ? breakDuration : sessionDuration; 
        updateTimerDisplay();
        updateButtonState();
    }

    private void skipSession() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }

        if (isBreak) {
            isBreak = false;
            timeRemaining = sessionDuration;
        } else {
            completedSessions++;
            isBreak = true;
            timeRemaining = (completedSessions % 4 == 0) ? 15 * 60 : breakDuration;
        }

        isRunning = false;
        updateTimerDisplay();
        updateButtonState();
        buildView();
    }

    private void sessionComplete() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        // Update UI di thread JavaFX
        javafx.application.Platform.runLater(() -> {
            if (!isBreak) {
                completedSessions++;
                userProfile.recordPomodoro();
                userProfile.recordProductivity(sessionDuration / 60);

                int nextBreakDuration = (completedSessions % 4 == 0) ? 15 * 60 : breakDuration;

                showCompletionAlert("🎉 Focus Session Complete!",
                        "Great work! Time for a " + (nextBreakDuration / 60) + "-minute break.");

                isBreak = true;
                timeRemaining = nextBreakDuration;

                if (currentInstance != null) {
                    currentInstance.buildView();
                }

                startTimer();

            } else {
                showCompletionAlert("✨ Break Complete!", "Ready to focus again?");

                isBreak = false;
                timeRemaining = sessionDuration;
                isRunning = false;

                if (currentInstance != null) {
                    currentInstance.buildView();
                }
            }
        });
    }

    private void showCompletionAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     *  Update timer display - called every second
     */
    private void updateTimerDisplay() {
        if (timerLabel != null) {
            timerLabel.setText(formatTime(timeRemaining));
        }

        if (modeLabel != null) {
            modeLabel.setText(isBreak ? "Break Time" : "Focus Time");
        }

        if (progressArc != null) {
            int totalDuration = isBreak ? breakDuration : sessionDuration;
            double progress = 1.0 - ((double) timeRemaining / totalDuration);
            double angle = progress * 360;
            progressArc.setLength(-angle);

            if (isBreak) {
                progressArc.setStroke(Color.web("#a6e3a1"));
            } else {
                progressArc.setStroke(Color.web("#89b4fa"));
            }
        }
    }

    /**
     * ⭐ Update button state
     */
    private void updateButtonState() {
        if (startPauseBtn != null) {
            if (isRunning) {
                startPauseBtn.setText("Pause");
                startPauseBtn.setStyle("-fx-background-color: #fab387; -fx-text-fill: #1e1e2e; " +
                        "-fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                String btnText = (timeRemaining == sessionDuration || timeRemaining == breakDuration) ? "Start"
                        : "Resume";
                startPauseBtn.setText(btnText);
                startPauseBtn.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #1e1e2e; " +
                        "-fx-font-size: 18px; -fx-font-weight: bold;");
            }
        }
    }

    // format waktu dari detik ke mm:ss
    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    public BorderPane getView() {
        return view;
    }
}

