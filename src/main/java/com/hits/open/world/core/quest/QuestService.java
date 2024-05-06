package com.hits.open.world.core.quest;

import com.hits.open.world.core.file.FileMetadata;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.core.quest.repository.QuestRepository;
import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.QuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestPhotoEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.core.quest.repository.entity.review.QuestReviewEntity;
import com.hits.open.world.core.quest.repository.entity.review.ReviewPhotoEntity;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.file.UploadFileDto;
import com.hits.open.world.public_interface.quest.CreateQuestDto;
import com.hits.open.world.public_interface.quest.UpdateQuestDto;
import com.hits.open.world.public_interface.quest.review.AddImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.CreateQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.UpdateQuestReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void createQuest(CreateQuestDto dto) {
        var questEntity = new QuestEntity(
                null,
                dto.name(),
                dto.description(),
                DifficultyType.fromString(dto.difficultyType()),
                QuestType.fromString(dto.questType()),
                TransportType.valueOf(dto.transportType())
        );
        var questInDb = questRepository.createQuest(questEntity);
        for (var image : dto.images()) {
            saveQuestImage(image, questInDb.questId());
        }
    }

    @Transactional
    public void deleteQuest(Long questId) {
        var quest = questRepository.getQuestById(questId)
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));
        questRepository.deleteQuest(questId);
        var questPhotos = questRepository.getQuestPhotosByQuestId(questId);
        for (var questPhoto : questPhotos) {
            deleteQuestImage(quest.questId(), questPhoto.questPhotoId());
        }
    }

    @Transactional
    public void updateQuest(UpdateQuestDto dto) {
        var quest = questRepository.getQuestById(dto.questId())
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));
        var updatedEntity = new QuestEntity(
                dto.questId(),
                dto.name().orElse(quest.name()),
                dto.description().orElse(quest.description()),
                dto.difficultyType().map(DifficultyType::fromString).orElse(quest.difficultyType()),
                dto.questType().map(QuestType::fromString).orElse(quest.questType()),
                dto.transportType().map(TransportType::valueOf).orElse(quest.transportType())
        );
        questRepository.updateQuest(updatedEntity);
    }

    public void startQuest(Long questId, String userId) {
        //TODO: тут какой-то должен быть механизм отслеживания начала квеста
    }

    public void finishQuest(Long questId, String userId) {
        //TODO: тут какой-то должен быть механизм отслеживания завершения квеста, в сервисе
    }

    public void saveQuestImage(MultipartFile image, Long questId) {
        var questPhotoEntity = new QuestPhotoEntity(null, questId);
        var questPhotoInDb = questRepository.createQuestPhoto(questPhotoEntity);
        var fileName = String.format("quest_%d_photo_%d", questId, questPhotoInDb.questPhotoId());
        saveImage(image, fileName);
    }

    public void deleteQuestImage(Long questId, Long questPhotoId) {
        var questPhoto = questRepository.getQuestPhotoById(questPhotoId)
                .orElseThrow(() -> new ExceptionInApplication("Quest photo not found", ExceptionType.NOT_FOUND));
        if (!questPhoto.questId().equals(questId)) {
            throw new ExceptionInApplication("Quest photo not found", ExceptionType.NOT_FOUND);
        }
        questRepository.deleteQuestPhoto(questPhotoId);
        var fileName = String.format("quest_%d_photo_%d", questId, questPhotoId);
        deleteImage(fileName);
    }

    public void createQuestReview(CreateQuestReviewDto dto) {
        if (questRepository.isQuestReviewExists(dto.questId(), dto.clientId())) {
            throw new ExceptionInApplication("Quest review already exists", ExceptionType.ALREADY_EXISTS);
        }

        var questReviewEntity = new QuestReviewEntity(
                null,
                dto.score(),
                dto.message(),
                dto.clientId(),
                dto.questId()
        );
        var questReviewInDb = questRepository.createQuestReview(questReviewEntity);
        for (var image : dto.images()) {
            var addImageQuestReviewDto = new AddImageQuestReviewDto(
                    questReviewInDb.questId(),
                    questReviewInDb.questReviewId(),
                    dto.clientId(),
                    image
            );
            addImageQuestReview(addImageQuestReviewDto);
        }
    }

    public void deleteQuestReview(DeleteQuestReviewDto dto) {
        var questReview = questRepository.getQuestReviewById(dto.questReviewId())
                .orElseThrow(() -> new ExceptionInApplication("Quest review not found", ExceptionType.NOT_FOUND));

        if (!questReview.clientId().equals(dto.clientId())) {
            throw new ExceptionInApplication("It's not your review", ExceptionType.FORBIDDEN);
        }

        questRepository.deleteQuestReview(dto.questReviewId());
        var reviewPhotos = questRepository.getReviewPhotosByReviewId(dto.questReviewId());
        for (var reviewPhoto : reviewPhotos) {
            var deleteImageQuestReviewDto = new DeleteImageQuestReviewDto(
                    dto.questId(),
                    dto.questReviewId(),
                    dto.clientId(),
                    reviewPhoto.reviewPhotoId()
            );
            deleteImageQuestReview(deleteImageQuestReviewDto);
        }
    }

    public void updateQuestReview(UpdateQuestReviewDto dto) {
        var questReview = questRepository.getQuestReviewById(dto.questReviewId())
                .orElseThrow(() -> new ExceptionInApplication("Quest review not found", ExceptionType.NOT_FOUND));

        if (!questReview.clientId().equals(dto.clientId())) {
            throw new ExceptionInApplication("It's not your review", ExceptionType.FORBIDDEN);
        }

        var updatedEntity = new QuestReviewEntity(
                dto.questReviewId(),
                dto.score().orElse(questReview.score()),
                dto.message().orElse(questReview.message()),
                questReview.clientId(),
                questReview.questId()
        );
        questRepository.updateQuestReview(updatedEntity);
    }

    public void addImageQuestReview(AddImageQuestReviewDto dto) {
        var questReview = questRepository.getQuestReviewById(dto.reviewId())
                .orElseThrow(() -> new ExceptionInApplication("Quest review not found", ExceptionType.NOT_FOUND));

        if (!questReview.clientId().equals(dto.userId())) {
            throw new ExceptionInApplication("It's not your review", ExceptionType.FORBIDDEN);
        }

        var imageEntity = new ReviewPhotoEntity(
                null,
                dto.reviewId()
        );
        var imageInDb = questRepository.createReviewPhoto(imageEntity);
        var fileName = String.format("quest_review_%d_photo_%d", questReview.questReviewId(), imageInDb.reviewPhotoId());
        saveImage(dto.image(), fileName);
    }

    public void deleteImageQuestReview(DeleteImageQuestReviewDto dto) {
        var questReview = questRepository.getQuestReviewById(dto.reviewId())
                .orElseThrow(() -> new ExceptionInApplication("Quest review not found", ExceptionType.NOT_FOUND));

        if (!questReview.clientId().equals(dto.userId())) {
            throw new ExceptionInApplication("It's not your review", ExceptionType.FORBIDDEN);
        }

        var reviewPhoto = questRepository.getReviewPhotosByReviewId(dto.reviewId())
                .stream()
                .filter(photo -> photo.reviewPhotoId().equals(dto.imageId()))
                .findFirst()
                .orElseThrow(() -> new ExceptionInApplication("Review photo not found", ExceptionType.NOT_FOUND));

        questRepository.deleteReviewPhoto(dto.imageId());
        var fileName = String.format("quest_review_%d_photo_%d", questReview.questReviewId(), reviewPhoto.reviewPhotoId());
        deleteImage(fileName);
    }

    private void saveImage(MultipartFile image, String fileName) {
        var fileMetadata = new FileMetadata(
                fileName,
                image.getContentType(),
                image.getSize()
        );
        var uploadFileDto = new UploadFileDto(
                fileMetadata,
                image
        );
        fileStorageService.uploadFile(uploadFileDto).subscribe();
    }

    private void deleteImage(String fileName) {
        fileStorageService.deleteFile(fileName).subscribe();
    }
}