package com.hits.open.world.core.quest.repository.entity.quest.mapper;

import com.example.open_the_world.public_.tables.records.ReviewPhotoRecord;
import com.hits.open.world.core.quest.repository.entity.review.ReviewPhotoEntity;
import org.jooq.RecordMapper;

public class ReviewPhotoEntityMapper implements RecordMapper<ReviewPhotoRecord, ReviewPhotoEntity> {
    @Override
    public ReviewPhotoEntity map(ReviewPhotoRecord reviewPhotoRecord) {
        return new ReviewPhotoEntity(
                reviewPhotoRecord.getPhotoId(),
                reviewPhotoRecord.getReviewId()
        );
    }
}
