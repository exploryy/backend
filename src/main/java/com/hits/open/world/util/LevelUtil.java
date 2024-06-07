package com.hits.open.world.util;

public final class LevelUtil {

    private LevelUtil() {
        throw new UnsupportedOperationException();
    }

    public static int calculateTotalExperienceInLevel(int level) {
        if (level == 0) {
            return 3000;
        }
        return calculateTotalExperienceInLevel(level - 1) + (3000 + 3000 * (level - 1));
    }

    public static int calculateLevel(int experience) {
        return calculateLevelHelper(experience, 0);
    }

    private static int calculateLevelHelper(int experience, int level) {
        int requiredExperience = calculateTotalExperienceInLevel(level);
        if (experience < requiredExperience) {
            return level;
        }
        return calculateLevelHelper(experience, level + 1);
    }
}
