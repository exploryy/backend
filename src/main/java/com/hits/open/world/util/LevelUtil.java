package com.hits.open.world.util;

public final class LevelUtil {

    private LevelUtil() {
        throw new UnsupportedOperationException();
    }

    public static int calculateLevel(int experience) {
        return (int) Math.floor((Math.sqrt(8 * experience + 1) - 1) / 2);
    }
}
