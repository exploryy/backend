package com.hits.open.world.core.buff.repository.enums;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;

public enum BuffStatus {
    COINS,
    EXPERIENCE;

    public static BuffStatus fromString(String status) {
        try {
            return BuffStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Invalid buff status: " + status, ExceptionType.INVALID);
        }
    }
}
