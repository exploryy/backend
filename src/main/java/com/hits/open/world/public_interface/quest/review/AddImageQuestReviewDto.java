package com.hits.open.world.public_interface.quest.review;

import org.springframework.web.multipart.MultipartFile;

public record AddImageQuestReviewDto(
        Long questId,
        Long reviewId,
        String userId,
        MultipartFile image
) {
}
