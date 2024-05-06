package com.hits.open.world.public_interface.quest;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateQuestDto(
        String name,
        String description,
        String difficultyType,
        String questType,
        String transportType,
        List<MultipartFile> images
) {
}
