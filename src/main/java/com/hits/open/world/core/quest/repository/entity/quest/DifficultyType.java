package com.hits.open.world.core.quest.repository.entity.quest;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;

public enum DifficultyType {
    EASY,
    MEDIUM,
    HARD,
    ;

    public static DifficultyType fromString(String value) {
        try {
            return DifficultyType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Invalid difficulty type: " + value, ExceptionType.INVALID);
        }
    }

    public static DifficultyType getRandonDifficultyType() {
        return DifficultyType.values()[(int) (Math.random() * DifficultyType.values().length)];
    }
}
