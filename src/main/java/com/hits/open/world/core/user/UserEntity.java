package com.hits.open.world.core.user;

public record UserEntity(
        String id,
        String username,
        String email,
        String password
) {
}
