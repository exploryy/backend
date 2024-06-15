package com.hits.open.world.core.quest.repository.entity.quest;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum TransportType {
    WALK(1.0),
    BICYCLE(0.5),
    CAR(0.1)
    ;

    private final double experienceBuff;

    public static TransportType fromString(String value) {
        try {
            return TransportType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Invalid transport type: " + value, ExceptionType.INVALID);
        }
    }

    public static TransportType fromSpeedMetersInSeconds(double speedMetersInSeconds) {
        if (speedMetersInSeconds < 2.7) {
            return WALK;
        } else if (speedMetersInSeconds < 6.9) {
            return BICYCLE;
        } else {
            return CAR;
        }
    }

    public static TransportType getRandomTransportType() {
        return TransportType.values()[(int) (Math.random() * TransportType.values().length)];
    }
}
