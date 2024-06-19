package com.hits.open.world.public_interface.multipolygon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;

import java.math.BigDecimal;

public record CreatePolygonResponseDto(
        GeoDto geo,

        @JsonProperty("area_percent")
        BigDecimal areaPercent
) {
}
