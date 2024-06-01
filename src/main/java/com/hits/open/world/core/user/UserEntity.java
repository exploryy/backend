package com.hits.open.world.core.user;

public record UserEntity(
        String id,
        String username,
        String email,
        String password
) {
    public String getPhotoName() {
        return "user_photo_%s".formatted(id);
    }
}
