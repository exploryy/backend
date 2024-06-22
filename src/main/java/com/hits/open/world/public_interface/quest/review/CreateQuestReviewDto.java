package com.hits.open.world.public_interface.quest.review;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public record CreateQuestReviewDto(
        Long questId,
        Integer score,
        Optional<String> message,
        String clientId,
        List<MultipartFile> images
) {
}
