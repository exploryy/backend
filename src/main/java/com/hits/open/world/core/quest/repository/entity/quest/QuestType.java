package com.hits.open.world.core.quest.repository.entity.quest;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;

public enum QuestType {
    POINT_TO_POINT,
    DISTANCE,
    ;

    public static QuestType fromString(String value) {
        try {
            return QuestType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Invalid quest type: " + value, ExceptionType.INVALID);
        }
    }
}
