package com.hits.open.world.public_interface.user;

import java.util.Optional;

public record ProfileDto(
        String userId,
        String username,
        String email,
        Optional<String> avatarUrl
) {
}
