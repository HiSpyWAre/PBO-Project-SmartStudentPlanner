package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;
import model.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TasksView {
    private BorderPane view;
    private TaskManager taskManager;
    private TableView<Task> taskTable;
    private ObservableList<Task> taskData;
    private ComboBox<String> statusFilterCombo;  
    private ComboBox<String> priorityFilterCombo; 
    
    public TasksView(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.view = new BorderPane();
        this.taskData = FXCollections.observableArrayList(taskManager.getAllTasks());
        buildView();
    }
    
    private void buildView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        // Header with title and add button
        HBox header = createHeader();
        
        // Filter options
        HBox filters = createFilters();
        
        // Task table
        taskTable = createTaskTable();
        VBox.setVgrow(taskTable, Priority.ALWAYS);
        
        content.getChildren().addAll(header, filters, taskTable);
        view.setCenter(content);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📋 All Tasks");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addBtn = new Button("+ Add Task");
        addBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; " +
                       "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20;");
        addBtn.setOnAction(e -> showAddTaskDialog());
        
        header.getChildren().addAll(title, spacer, addBtn);
        return header;
    }
    
    private HBox createFilters() {
        HBox filters = new HBox(15);
        filters.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 14px;");
        
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "To Do", "In Progress", "Completed", "Overdue");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4;");
        statusFilter.setOnAction(e -> filterTasks(statusFilter.getValue()));
        
        ComboBox<String> priorityFilter = new ComboBox<>();
        priorityFilter.getItems().addAll("All Priorities", "Urgent", "High", "Medium", "Low");
        priorityFilter.setValue("All Priorities");
        priorityFilter.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4;");
        priorityFilter.setOnAction(e -> applyFilters(statusFilter.getValue(), priorityFilter.getValue()));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search tasks...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; " +
                            "-fx-prompt-text-fill: #6c7086;");
        searchField.textProperty().addListener((obs, old, newVal) -> searchTasks(newVal));
        
        filters.getChildren().addAll(filterLabel, statusFilter, priorityFilter, searchField);
        return filters;
    }
    
        private void applyFilters(String statusFilter, String priorityFilter) {
        taskData.clear();
        
        List<Task> filtered = taskManager.getAllTasks();
        
        // Apply status filter
        if (!statusFilter.equals("All")) {
            filtered = filtered.stream()
                .filter(t -> {
                    TaskStatus status = switch (statusFilter) {
                        case "To Do" -> TaskStatus.TODO;
                        case "In Progress" -> TaskStatus.IN_PROGRESS;
                        case "Completed" -> TaskStatus.COMPLETED;
                        case "Overdue" -> TaskStatus.OVERDUE;
                        default -> null;
                    };
                    return status != null && t.getStatus() == status;
                })
                .toList();
        }
        
        // Apply priority filter
        if (!priorityFilter.equals("All Priorities")) {
            filtered = filtered.stream()
                .filter(t -> {
                    TaskPriority priority = switch (priorityFilter) {
                        case "Urgent" -> TaskPriority.URGENT;
                        case "High" -> TaskPriority.HIGH;
                        case "Medium" -> TaskPriority.MEDIUM;
                        case "Low" -> TaskPriority.LOW;
                        default -> null;
                    };
                    return priority != null && t.getPriority() == priority;
                })
                .toList();
        }
        
        taskData.addAll(filtered);
    }

    @SuppressWarnings("unchecked")
    private TableView<Task> createTaskTable() {
        TableView<Task> table = new TableView<>();
        table.setItems(taskData);
        table.setStyle("-fx-background-color: #313244; -fx-control-inner-background: #313244;");
        
        // Checkbox column
        TableColumn<Task, Boolean> checkCol = new TableColumn<>("");
        checkCol.setPrefWidth(50);
        checkCol.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Task task = getTableRow().getItem();
                    checkBox.setSelected(task.getStatus() == TaskStatus.COMPLETED);
                    checkBox.setOnAction(e -> {
                        if (checkBox.isSelected()) {
                            task.markComplete();
                        } else {
                            task.setStatus(TaskStatus.TODO);
                        }
                        taskManager.updateTask(task);
                        table.refresh();
                    });
                    setGraphic(checkBox);
                }
            }
        });
        
        // Title column
        TableColumn<Task, String> titleCol = new TableColumn<>("Task");
        titleCol.setPrefWidth(300);
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setStyle("-fx-text-fill: #cdd6f4;");
        
        // Type column
        TableColumn<Task, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(120);
        typeCol.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getClass().getSimpleName();
            return new javafx.beans.property.SimpleStringProperty(type);
        });
        
        // Priority column
        TableColumn<Task, TaskPriority> priorityCol = new TableColumn<>("Priority");
        priorityCol.setPrefWidth(100);
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(TaskPriority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(priority.toString());
                    String color = switch (priority) {
                        case URGENT -> "-fx-text-fill: #f38ba8; -fx-font-weight: bold;";
                        case HIGH -> "-fx-text-fill: #fab387; -fx-font-weight: bold;";
                        case MEDIUM -> "-fx-text-fill: #f9e2af;";
                        case LOW -> "-fx-text-fill: #a6e3a1;";
                    };
                    setStyle(color);
                }
            }
        });
        
        // Due date column
        TableColumn<Task, LocalDateTime> dueCol = new TableColumn<>("Due Date");
        dueCol.setPrefWidth(180);
        dueCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                    if (date.isBefore(LocalDateTime.now())) {
                        setStyle("-fx-text-fill: #f38ba8;");
                    } else {
                        setStyle("-fx-text-fill: #cdd6f4;");
                    }
                }
            }
        });
        
        // Status column
        TableColumn<Task, TaskStatus> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(120);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(TaskStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString().replace("_", " "));
                    String color = switch (status) {
                        case COMPLETED -> "-fx-text-fill: #a6e3a1;";
                        case IN_PROGRESS -> "-fx-text-fill: #89b4fa;";
                        case OVERDUE -> "-fx-text-fill: #f38ba8;";
                        default -> "-fx-text-fill: #cdd6f4;";
                    };
                    setStyle(color);
                }
            }
        });
        
        // Estimated hours column
        TableColumn<Task, Integer> hoursCol = new TableColumn<>("Est. Hours");
        hoursCol.setPrefWidth(100);
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("estimatedHours"));
        hoursCol.setStyle("-fx-text-fill: #cdd6f4;");
        
        // Actions column
        TableColumn<Task, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(10, editBtn, deleteBtn);
            
            {
                editBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e; -fx-font-size: 11px;");
                deleteBtn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: #1e1e2e; -fx-font-size: 11px;");
                
                editBtn.setOnAction(e -> {
                    Task task = getTableRow().getItem();
                    if (task != null) showEditTaskDialog(task);
                });
                
                deleteBtn.setOnAction(e -> {
                    Task task = getTableRow().getItem();
                    if (task != null) {
                        taskManager.removeTask(task);
                        taskData.remove(task);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        
        table.getColumns().addAll(checkCol, titleCol, typeCol, priorityCol, dueCol, statusCol, hoursCol, actionsCol);
        return table;
    }
    
    private void showAddTaskDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");
        dialog.setHeaderText("Create a new task");
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Assignment", "Exam", "Project");
        typeBox.setValue("Assignment");
        
        ComboBox<TaskPriority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(TaskPriority.values());
        priorityBox.setValue(TaskPriority.MEDIUM);
        
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
        Spinner<Integer> estimatedHoursSpinner = new Spinner<>(1, 100, 2);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeBox, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityBox, 1, 3);
        grid.add(new Label("Due Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Due Hour:"), 0, 5);
        grid.add(hourSpinner, 1, 5);
        grid.add(new Label("Estimated Hours:"), 0, 6);
        grid.add(estimatedHoursSpinner, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                System.out.println("Selected priority: " + priorityBox.getValue());
                
                LocalDateTime dueDateTime = LocalDateTime.of(
                    datePicker.getValue(), 
                    LocalTime.of(hourSpinner.getValue(), 0)
                );
                
                Task task = switch (typeBox.getValue()) {
                    case "Assignment" -> new Assignment(
                        titleField.getText(),
                        descField.getText(),
                        dueDateTime,
                        estimatedHoursSpinner.getValue(),
                        priorityBox.getValue()
                    );
                    case "Exam" -> new Exam(
                        titleField.getText(),
                        descField.getText(),
                        dueDateTime,
                        estimatedHoursSpinner.getValue()
                    );
                    case "Project" -> new Project(
                        titleField.getText(),
                        descField.getText(),
                        dueDateTime,
                        estimatedHoursSpinner.getValue(),
                        priorityBox.getValue()
                    );
                    default -> null;
                };

                System.out.println("Task priority: " + task.getPriority());
                
                return task;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(task -> {
            taskManager.addTask(task);
            taskData.add(task);
        });
    }
    
    private void showEditTaskDialog(Task task) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Editing: " + task.getTitle());
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // PRE-FILL with existing values
        TextField titleField = new TextField(task.getTitle());
        titleField.setPromptText("Task title");
        
        TextArea descField = new TextArea(task.getDescription());
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        
        ComboBox<TaskPriority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(TaskPriority.values());
        priorityBox.setValue(task.getPriority());
        
        DatePicker datePicker = new DatePicker(task.getDueDate().toLocalDate());
        
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, task.getDueDate().getHour());
        
        Spinner<Integer> estimatedHoursSpinner = new Spinner<>(1, 100, task.getEstimatedHours());
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityBox, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Due Hour:"), 0, 4);
        grid.add(hourSpinner, 1, 4);
        grid.add(new Label("Estimated Hours:"), 0, 5);
        grid.add(estimatedHoursSpinner, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Update task properties
                task.setTitle(titleField.getText());
                task.setDescription(descField.getText());
                task.setPriority(priorityBox.getValue());
                
                LocalDateTime newDueDate = LocalDateTime.of(
                    datePicker.getValue(),
                    LocalTime.of(hourSpinner.getValue(), 0)
                );
                task.setDueDate(newDueDate);
                task.setEstimatedHours(estimatedHoursSpinner.getValue());
                
                return true;
            }
            return false;
        });
        
        dialog.showAndWait().ifPresent(saved -> {
            if (saved) {
                taskManager.updateTask(task);
                taskTable.refresh(); // Refresh table to show changes
                
                // Show success message
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Task updated successfully!");
                success.showAndWait();
            }
        });
    }
    
    private void filterTasks(String filter) {
        taskData.clear();
        List<Task> filtered = switch (filter) {
            case "To Do" -> taskManager.getTasksByStatus(TaskStatus.TODO);
            case "In Progress" -> taskManager.getTasksByStatus(TaskStatus.IN_PROGRESS);
            case "Completed" -> taskManager.getTasksByStatus(TaskStatus.COMPLETED);
            case "Overdue" -> taskManager.getTasksByStatus(TaskStatus.OVERDUE);
            default -> taskManager.getAllTasks();
        };
        taskData.addAll(filtered);
    }
    
    private void searchTasks(String query) {
        if (query.isEmpty()) {
            taskData.setAll(taskManager.getAllTasks());
        } else {
            taskData.setAll(taskManager.getAllTasks().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                            t.getDescription().toLowerCase().contains(query.toLowerCase()))
                .toList());
        }
    }
    
    public BorderPane getView() {
        return view;
    }
}
