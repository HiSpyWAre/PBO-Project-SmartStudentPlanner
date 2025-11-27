// user profile model untuk menyimpan data pengguna
package model;

import java.time.LocalDate;
// import java.time.LocalDateTime;
import java.util.*;

public class UserProfile {
    private String username;
    private int xp;
    private int level;
    private int streak;
    private LocalDate lastActivityDate;
    private Map<LocalDate, Integer> productivityHistory;
    private List<Achievement> achievements;
    private List<Integer> pomodoroHistory;
    private Map<String, Double> subjectTimeDistribution;
    
    public UserProfile(String username) {
        this.username = username;
        this.xp = 0;
        this.level = 1;
        this.streak = 0;
        this.lastActivityDate = LocalDate.now();
        this.productivityHistory = new HashMap<>();
        this.achievements = new ArrayList<>();
        this.pomodoroHistory = new ArrayList<>();
        this.subjectTimeDistribution = new HashMap<>();
        
        initializeAchievements();
    }
    
    // inisialisasi daftar achievement dan kondisinya
    private void initializeAchievements() {
        achievements.add(new Achievement("First Steps", "Complete your first task", 50, false));
        achievements.add(new Achievement("Dedicated", "Maintain a 7-day streak", 200, false));
        achievements.add(new Achievement("Marathon Runner", "Complete a 10-hour study session", 300, false));
        achievements.add(new Achievement("Early Bird", "Start studying before 7 AM", 100, false));
        achievements.add(new Achievement("Night Owl", "Study past midnight", 100, false));
        achievements.add(new Achievement("Perfectionist", "Complete 10 tasks on time", 250, false));
        achievements.add(new Achievement("Master", "Reach level 10", 500, false));
    }
    
    // menambahkan XP dan mengecek level up
    public void addXP(int amount) {
        xp += amount;
        checkLevelUp();
        checkAchievements();
    }
    
    // mengecek dan menangani level up
    private void checkLevelUp() {
        int xpNeeded = level * 100;
        if (xp >= xpNeeded) {
            level++;
            xp -= xpNeeded;
            System.out.println("Level up! You are now level " + level);
        }
    }
    
    // memperbarui streak belajar yg di display di dashboard
    public void updateStreak() {
        LocalDate today = LocalDate.now();
        
        if (lastActivityDate.plusDays(1).equals(today)) {
            streak++;
        } else if (!lastActivityDate.equals(today)) {
            streak = 1;
        }
        
        lastActivityDate = today;
        checkAchievements();
    }
    
    // merekam produktivitas belajar harian
    public void recordProductivity(int minutesStudied) {
        LocalDate today = LocalDate.now();
        productivityHistory.merge(today, minutesStudied, Integer::sum);
        updateStreak();
    }
    
    // merekam sesi pomodoro
    public void recordPomodoro() {
        pomodoroHistory.add(25); // Standard 25-minute session
        addXP(10);
    }
    
    // memperbarui distribusi waktu berdasarkan subject (matkul)
    private void checkAchievements() {
        for (Achievement achievement : achievements) {
            if (!achievement.isUnlocked() && achievement.checkCondition(this)) {
                achievement.unlock();
                addXP(achievement.getXpReward());
                System.out.println("Achievement unlocked: " + achievement.getName());
            }
        }
    }
    
    // mendapatkan produktivitas minggu lalu
    public Map<LocalDate, Integer> getLastWeekProductivity() {
        Map<LocalDate, Integer> lastWeek = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        // mengisi data untuk 7 hari terakhir
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            lastWeek.put(date, productivityHistory.getOrDefault(date, 0));
        }
        
        return lastWeek;
    }
    
    public int getTotalPomodoroSessions() {
        return pomodoroHistory.size();
    }
    
    public int getTotalStudyMinutes() {
        return productivityHistory.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    // Getters
    public String getUsername() { 
        return username; 
    }

    public int getXP() { 
        return xp; 
    }

    public int getLevel() { 
        return level; 
    }

    public int getStreak() { 
        return streak; 
    }

    // generic getter untuk achievements
    public List<Achievement> getAchievements() { 
        return achievements; 
    }

    // getter untuk achievements yang sudah di-unlock
    public List<Achievement> getUnlockedAchievements() {
        return achievements.stream().filter(Achievement::isUnlocked).toList();
    }

    // getter untuk productivity history
    public Map<LocalDate, Integer> getProductivityHistory() { 
        return productivityHistory; 
    }
}
