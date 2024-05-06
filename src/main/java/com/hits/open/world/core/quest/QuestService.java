package com.hits.open.world.core.quest;

import com.hits.open.world.core.file.FileMetadata;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.core.quest.repository.QuestRepository;
import com.hits.open.world.core.quest.repository.entity.quest.QuestPhotoEntity;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.file.UploadFileDto;
import com.hits.open.world.public_interface.quest.CreateQuestDto;
import com.hits.open.world.public_interface.quest.UpdateQuestDto;
import com.hits.open.world.public_interface.quest.review.CreateQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.UpdateQuestReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final FileStorageService fileStorageService;

    public void createQuest(CreateQuestDto dto) {

    }

    public void deleteQuest(Long questId) {

    }

    public void updateQuest(UpdateQuestDto dto) {

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
        saveQuestImage(image, questPhotoInDb);
    }

    public void deleteQuestImage(Long questId, Long questPhotoId) {
        var questPhoto = questRepository.getQuestPhotoById(questPhotoId)
                .orElseThrow(() -> new ExceptionInApplication("Quest photo not found", ExceptionType.NOT_FOUND));
        if (!questPhoto.questId().equals(questId)) {
            throw new ExceptionInApplication("Quest photo not found", ExceptionType.NOT_FOUND);
        }
        questRepository.deleteQuestPhoto(questPhotoId);
        deleteQuestImage(questPhoto);
    }

    public void createQuestReview(CreateQuestReviewDto dto) {

    }

    public void deleteQuestReview(DeleteQuestReviewDto dto) {

    }

    public void updateQuestReview(UpdateQuestReviewDto dto) {

    }

    private void saveQuestImage(MultipartFile image, QuestPhotoEntity entity) {
        var fileMetadata = new FileMetadata(
                String.format("quest-%d-%d", entity.questId(), entity.questPhotoId()),
                image.getContentType(),
                image.getSize()
        );
        var uploadFileDto = new UploadFileDto(
                fileMetadata,
                image
        );
        fileStorageService.uploadFile(uploadFileDto).subscribe();
    }

    private void deleteQuestImage(QuestPhotoEntity entity) {
        var name = String.format("quest-%d-%d", entity.questId(), entity.questPhotoId());
        fileStorageService.deleteFile(name).subscribe();
    }
}