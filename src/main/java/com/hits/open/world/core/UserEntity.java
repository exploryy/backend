package com.hits.open.world.core;

public record UserEntity(
        String id,
        String username,
        String email,
        String password
) {
}
