package com.hits.open.world.public_interface.quest.review;

import com.hits.open.world.public_interface.user.ProfileDto;

import java.util.List;

public record QuestReviewDto(
        Long questReviewId,
        Integer score,
        String message,
        String clientId,
        Long questId,
        List<String> reviewPhotos,
        ProfileDto profile
) {
}
