import model.TaskManager;
import model.UserProfile;
// import model.Task;
import model.Assignment;
import model.TaskPriority;
// import model.TaskStatus;

public class TestImports {
    public static void main(String[] args) {
        // Test 1: Can we create TaskManager?
        TaskManager tm = new TaskManager();
        System.out.println("✅ TaskManager created successfully!");
        
        // Test 2: Can we create UserProfile?
        UserProfile up = new UserProfile("Test User");
        System.out.println("✅ UserProfile created successfully!");
        
        // Test 3: Can we create a Task?
        Assignment task = new Assignment(
            "Test Task",
            "Description",
            java.time.LocalDateTime.now().plusDays(1),
            2,
            TaskPriority.HIGH
        );
        System.out.println("✅ Task created successfully!");
        
        // Test 4: Can we add task?
        tm.addTask(task);
        System.out.println("✅ Task added successfully!");
        
        System.out.println("\n🎉 ALL IMPORTS WORKING!");
        System.out.println("Total tasks: " + tm.getAllTasks().size());
        System.out.println("User level: " + up.getLevel());
    }
}