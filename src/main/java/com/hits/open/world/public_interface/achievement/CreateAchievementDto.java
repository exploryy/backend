package com.hits.open.world.public_interface.achievement;

import org.springframework.web.multipart.MultipartFile;

public record CreateAchievementDto(
        String name,
        String description,
        MultipartFile image
) {
}
