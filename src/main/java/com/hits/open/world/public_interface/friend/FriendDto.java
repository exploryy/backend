package com.hits.open.world.public_interface.friend;

import java.util.Optional;

public record FriendDto(
        String userId,
        String username,
        String email,
        Optional<String> avatarUrl
) {
}
