package com.hits.open.world.core.quest.repository.entity.quest;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;

public enum TransportType {
    WALK,
    BICYCLE,
    ;

    public static TransportType fromString(String value) {
        try {
            return TransportType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Invalid transport type: " + value, ExceptionType.INVALID);
        }
    }
}
