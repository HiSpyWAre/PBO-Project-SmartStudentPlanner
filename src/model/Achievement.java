package model;

import java.time.LocalDateTime;

public class Achievement {
    private String name;
    private String description;
    private int xpReward;
    private boolean unlocked;
    private LocalDateTime unlockedDate;

    // constructor achievement mempunyai argumen:
    public Achievement(String name, String description, int xpReward, boolean unlocked) {
        this.name = name;
        this.description = description;
        this.xpReward = xpReward;
        this.unlocked = unlocked;
    }

    public boolean checkCondition(UserProfile profile) {
        // Cek kondisi spesifik berdasarkan nama achievement
        return switch (name) {
            case "First Steps" -> {
                yield false; // Akan di cek di MainController
            }

            case "Dedicated" -> profile.getStreak() >= 7;
            case "Marathon Runner" -> profile.getLevel() >= 10;
            case "Master" -> profile.getProductivityHistory().values().stream()
                    .anyMatch(minutes -> minutes >= 600);
            default -> false;
        };
    }

    public void unlock() {
        this.unlocked = true;
        this.unlockedDate = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getXpReward() {
        return xpReward;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public LocalDateTime getUnlockedDate() {
        return unlockedDate;
    }
}
