package com.hits.open.world.public_interface.user;

public record CreateUserDto(
        String username,
        String email,
        String password
) {
}
