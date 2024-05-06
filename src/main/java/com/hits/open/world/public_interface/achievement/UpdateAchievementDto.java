package com.hits.open.world.public_interface.achievement;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record UpdateAchievementDto(
        Long achievementId,
        Optional<String> name,
        Optional<String> description,
        Optional<MultipartFile> image
) {
}
