package com.hits.open.world.core.buff.repository.entity;

import com.hits.open.world.core.buff.repository.enums.BuffStatus;

import java.math.BigDecimal;

public record BuffEntity(
        Long buffId,
        BigDecimal valueFactor,
        BuffStatus status,
        int levelNumber
) {
}
