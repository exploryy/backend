package com.hits.open.world.public_interface.quest.review;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateQuestReviewDto(
        Long questId,
        Integer score,
        String message,
        String clientId,
        List<MultipartFile> photos
) {
}
