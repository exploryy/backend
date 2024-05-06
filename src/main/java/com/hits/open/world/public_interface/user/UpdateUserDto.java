package com.hits.open.world.public_interface.user;

import java.util.Optional;

public record UpdateUserDto(
        String userId,
        Optional<String> username,
        Optional<String> email,
        Optional<String> password
) {
}
