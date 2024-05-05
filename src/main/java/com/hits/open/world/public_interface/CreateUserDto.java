package com.hits.open.world.public_interface;

public record CreateUserDto(
        String username,
        String email,
        String password
) {
}
