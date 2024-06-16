package com.hits.open.world.public_interface.location;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LocationDto(
        String clientId,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
