package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import model.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Calendar View - menampilkan tugas di format kalender
public class CalendarView {
    private BorderPane view;
    private TaskManager taskManager;
    private YearMonth currentMonth;
    
    public CalendarView(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.currentMonth = YearMonth.now();
        this.view = new BorderPane();
        buildView();
    }
    
    // method untuk membangun tampilan kalender
    private void buildView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        // Header dengan navigasi per bulan
        HBox header = createHeader();
        
        // Calendar grid
        GridPane calendar = createCalendar();
        
        // Task list untuk hari terpilih
        VBox taskList = createTaskList();
        
        content.getChildren().addAll(header, calendar, taskList);
        view.setCenter(content);
    }
    
    // method untuk membuat header kalender
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER);
        
        // tombol geser bulan
        Button prevBtn = new Button("◀");
        prevBtn.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
        prevBtn.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            buildView();
        });
        
        // bulan dan tahun sekarang
        Label monthLabel = new Label(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        monthLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        // tombol geser bulan ke depan
        Button nextBtn = new Button("▶");
        nextBtn.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
        nextBtn.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            buildView();
        });
        
        Button todayBtn = new Button("Today");
        todayBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; -fx-font-weight: bold;");
        todayBtn.setOnAction(e -> {
            currentMonth = YearMonth.now(); // kembali ke bulan sekarang
            buildView();
        });
        
        // spacer untuk meratakan elemen
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // menambahkan elemen ke header
        header.getChildren().addAll(prevBtn, monthLabel, nextBtn, spacer, todayBtn);
        return header;
    }
    
    // method untuk membuat grid kalender
    private GridPane createCalendar() {
        GridPane calendar = new GridPane();
        calendar.setHgap(10);
        calendar.setVgap(10);
        
        // Day headers
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #a6adc8; -fx-font-size: 14px;");
            dayLabel.setPrefWidth(150);
            dayLabel.setAlignment(Pos.CENTER);
            calendar.add(dayLabel, i, 0);
        }
        
        // Calendar days
        LocalDate firstDay = currentMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();
        
        int row = 1;
        int col = dayOfWeek;
        
        // Looping buat setiap hari dalam bulan
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            VBox dayBox = createDayBox(date);
            calendar.add(dayBox, col, row);
            
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
        
        return calendar;
    }
    
    // method untuk membuat kotak hari individual 
    private VBox createDayBox(LocalDate date) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setPrefSize(150, 100);
        box.setAlignment(Pos.TOP_LEFT);
        
        // Highlight tanggal hari ini
        boolean isToday = date.equals(LocalDate.now());
        String bgColor = isToday ? "#45475a" : "#313244"; // jika hari ini, gunakan warna berbeda
        box.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 8; -fx-border-color: #45475a; -fx-border-radius: 8;");
        
        // Nomor hari/tanggal
        Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumber.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (isToday ? "#89b4fa" : "#cdd6f4") + ";");
        
        // display jumlah tugas pada hari tersebut
        long taskCount = taskManager.getAllTasks().stream()
            .filter(t -> t.getDueDate().toLocalDate().equals(date)) // filter tugas berdasarkan tanggal
            .count();
        
        // hanya tampilkan jika ada tugas
        if (taskCount > 0) {
            Label taskLabel = new Label(taskCount + " task" + (taskCount > 1 ? "s" : ""));
            taskLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #f9e2af;");
            box.getChildren().add(taskLabel);
        }
        
        // menambahkan nomor hari ke dalam kotak
        box.getChildren().add(0, dayNumber);
        return box;
    }
    
    // method untuk membuat daftar tugas di bulan tersebut
    private VBox createTaskList() {
        VBox list = new VBox(10);
        list.setPadding(new Insets(20));
        list.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        
        Label title = new Label("📅 Tasks This Month");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        // ambil semua tugas di bulan ini
        List<Task> monthTasks = taskManager.getAllTasks().stream()
            .filter(t -> YearMonth.from(t.getDueDate()).equals(currentMonth))
            .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate()))
            .toList();
        
        // jika tidak ada tugas, tampilkan pesan kosong
        if (monthTasks.isEmpty()) {
            Label empty = new Label("No tasks this month");
            empty.setStyle("-fx-text-fill: #a6adc8; -fx-font-style: italic;");
            list.getChildren().addAll(title, empty);
        } else { // tampilkan daftar tugas
            list.getChildren().add(title);
            for (Task task : monthTasks) {
                Label taskLabel = new Label("• " + task.getTitle() + " - " + 
                    task.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd")));
                taskLabel.setStyle("-fx-text-fill: #cdd6f4;");
                list.getChildren().add(taskLabel);
            }
        }
        
        return list;
    }
    
    public BorderPane getView() {
        return view;
    }
}

