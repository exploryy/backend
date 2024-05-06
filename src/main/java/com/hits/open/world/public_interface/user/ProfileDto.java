package com.hits.open.world.public_interface.user;

public record ProfileDto(
        String userId,
        String username,
        String email
) {
}
