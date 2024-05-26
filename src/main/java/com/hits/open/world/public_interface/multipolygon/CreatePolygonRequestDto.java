package com.hits.open.world.public_interface.multipolygon;

import com.hits.open.world.core.multipolygon.enums.FigureType;

import java.math.BigDecimal;

public record CreatePolygonRequestDto(
        BigDecimal longitude,
        BigDecimal latitude,
        FigureType figureType
) {
}
