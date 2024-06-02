package com.hits.open.world.util;

public final class LevelUtil {

    private LevelUtil() {
        throw new UnsupportedOperationException();
    }

    public static int calculateLevel(int experience) {
        if (experience < 3000) {
            return 0;
        }

        return (experience - 3000) / 5000 + 1;
    }
}
