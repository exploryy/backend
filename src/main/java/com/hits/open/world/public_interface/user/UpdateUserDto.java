package com.hits.open.world.public_interface.user;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record UpdateUserDto(
        String userId,
        Optional<String> username,
        Optional<String> email,
        Optional<String> password,
        Optional<MultipartFile> avatar
) {
}
