package controller;

import java.time.LocalDateTime;
import java.util.List;

import model.*;
import model.Task;

// Smart Scheduler - menghasilkan jadwal belajar cerdas berdasarkan tugas dan profil pengguna
class SmartScheduler {
    // private atribut untuk mengakses data tugas dan profil pengguna
    private TaskManager taskManager;
    private UserProfile userProfile;
    
    // konstruktor untuk inisialisasi atribut
    public SmartScheduler(TaskManager taskManager, UserProfile userProfile) {
        this.taskManager = taskManager;
        this.userProfile = userProfile;
    }
    
    // Menghasilkan jadwal belajar untuk jangka waktu tertentu (dalam hari)
    public List<ScheduledBlock> generateSchedule(int days) {
        List<ScheduledBlock> schedule = new java.util.ArrayList<>();
        List<Task> pendingTasks = taskManager.getTasksSortedByUrgency();
        
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime = currentTime.plusDays(days);
        
        // Simple algoritma greedy untk penjadwalan
        for (Task task : pendingTasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) continue;
            if (!task.canStart()) continue; // Check dependencies
            
            // 
            int hoursNeeded = task.getEstimatedHours() - task.getActualHours();
            
            // penjadwalan dalam blok 2 jam selama jam optimal (9 pagi - 9 malam)
            while (hoursNeeded > 0 && currentTime.isBefore(endTime)) {
                if (isProductiveHour(currentTime)) {
                    int blockHours = Math.min(2, hoursNeeded);
                    ScheduledBlock block = new ScheduledBlock(
                        task,
                        currentTime,
                        currentTime.plusHours(blockHours)
                    );
                    schedule.add(block);
                    hoursNeeded -= blockHours;
                    currentTime = currentTime.plusHours(blockHours);
                } else {
                    currentTime = currentTime.plusHours(1);
                }
            }
        }
        
        return schedule;
    }
    
    public List<Task> getRecommendedTasks(int count) {
        List<Task> urgentTasks = taskManager.getTasksSortedByUrgency();
        
        // Filter by what can be started now and is most urgent -> filter berdasarkan tugas yang bisa dimulai sekarang dan paling mendesak
        return urgentTasks.stream()
            .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
            .filter(Task::canStart)
            .limit(count)
            .toList();
    }
    
    // Cek apakah jam tersebut adalah jam produktif
    private boolean isProductiveHour(LocalDateTime time) {
        int hour = time.getHour();
        return hour >= 9 && hour <= 21; // 9 AM sampai 9 PM
    }
}

// blok terjadwal untuk tampilan kalender
class ScheduledBlock {
    private Task task;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public ScheduledBlock(Task task, LocalDateTime startTime, LocalDateTime endTime) {
        this.task = task;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public Task getTask() { 
        return task; 
    }

    public LocalDateTime getStartTime() { 
        return startTime; 
    }

    public LocalDateTime getEndTime() { 
        return endTime; 
    }
    
    // durasi blok dalam jam
    public int getDurationHours() {
        return (int) java.time.Duration.between(startTime, endTime).toHours(); // konversi ke jam
    }
}