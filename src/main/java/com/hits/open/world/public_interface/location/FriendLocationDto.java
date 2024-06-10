package com.hits.open.world.public_interface.location;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Optional;

@Builder
public record FriendLocationDto(
        String email,
        Optional<String> avatarUrl,
        String username,
        String userId,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
