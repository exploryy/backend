package com.hits.open.world.public_interface.user_location;

import java.math.BigDecimal;

public record LocationDto(
        String clientId,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
