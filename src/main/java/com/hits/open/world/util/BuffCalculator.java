package com.hits.open.world.util;

import static com.hits.open.world.util.LevelUtil.calculateLevel;

public final class BuffCalculator {

    private BuffCalculator() {
        throw new UnsupportedOperationException();
    }

    public static int calculateExperience(int currentExperience, int newExperience) {
        int level = calculateLevel(currentExperience);
        int buffExperience = (int) (0.2 * level) + newExperience;
        return currentExperience + buffExperience;
    }
}
