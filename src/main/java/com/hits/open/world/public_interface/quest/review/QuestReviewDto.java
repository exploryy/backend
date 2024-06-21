package com.hits.open.world.public_interface.quest.review;

import com.hits.open.world.public_interface.user.ProfileDto;

import java.util.List;
import java.util.Optional;

public record QuestReviewDto(
        Long questReviewId,
        Integer score,
        Optional<String> message,
        Long questId,
        List<String> reviewPhotos,
        ProfileDto profile
) {
}
