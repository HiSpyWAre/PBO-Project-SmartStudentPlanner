package model;

public interface ProfileObserver {
    void onXPChanged(int newXP, int newLevel);
    
    /**
     * Dipanggil ketika streak berubah
     */
    void onStreakChanged(int newStreak);
    
    /**
     * Dipanggil ketika achievement di-unlock
     */
    void onAchievementUnlocked(Achievement achievement);
    
    /**
     * Dipanggil ketika produktivitas di-record
     */
    void onProductivityRecorded(int minutes);
}
