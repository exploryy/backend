package com.hits.open.world.core.achievement;

import com.hits.open.world.core.achievement.repository.AchievementEntity;
import com.hits.open.world.core.achievement.repository.AchievementRepository;
import com.hits.open.world.core.achievement.repository.ClientAchievementEntity;
import com.hits.open.world.core.file.FileMetadata;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.public_interface.achievement.AchievementDto;
import com.hits.open.world.public_interface.achievement.CreateAchievementDto;
import com.hits.open.world.public_interface.achievement.UpdateAchievementDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.file.UploadFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void createAchievement(CreateAchievementDto dto) {
        checkAchievementExists(dto.name());
        var achievementEntity = new AchievementEntity(
                null,
                dto.name(),
                dto.description()
        );
        var achievementInDb = achievementRepository.createAchievement(achievementEntity);
        saveImage(dto.image(), achievementInDb.achievementId());
    }

    @Transactional
    public void deleteAchievement(Long achievementId) {
        checkAchievementExists(achievementId);
        achievementRepository.deleteAchievement(achievementId);
        deleteImage(achievementId);
    }

    @Transactional
    public void updateAchievement(UpdateAchievementDto dto) {
        var achievement = checkAchievementExists(dto.achievementId());
        var achievementEntity = new AchievementEntity(
                dto.achievementId(),
                dto.name().orElse(achievement.name()),
                dto.description().orElse(achievement.description())
        );
        achievementRepository.updateAchievement(achievementEntity);
        if (dto.image().isPresent()) {
            deleteImage(dto.achievementId());
            saveImage(dto.image().get(), dto.achievementId());
        }
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> getAchievements(String userId) {
        return achievementRepository.getAchievements(userId);
    }

    public void addAchievementToUser(String userId, Long achievementId) {
        checkAchievementExists(achievementId);
        var clientAchievementEntity = new ClientAchievementEntity(
                userId,
                achievementId,
                OffsetDateTime.now()
        );
        achievementRepository.createClientAchievement(clientAchievementEntity);
    }

    private AchievementEntity checkAchievementExists(Long achievementId) {
        var achievement = achievementRepository.getAchievementById(achievementId);
        if (achievement.isEmpty()) {
            throw new ExceptionInApplication("Achievement not found", ExceptionType.NOT_FOUND);
        }
        return achievement.get();
    }

    private AchievementEntity checkAchievementExists(String name) {
        var achievement = achievementRepository.getAchievementByName(name);
        if (achievement.isPresent()) {
            throw new ExceptionInApplication("Achievement already exists", ExceptionType.ALREADY_EXISTS);
        }
        return achievement.get();
    }

    private void saveImage(MultipartFile image, Long achievementId) {
        var fileMetadata = new FileMetadata(
                String.format("achievement_%d", achievementId),
                image.getContentType(),
                image.getSize()
        );
        var uploadFileDto = new UploadFileDto(
                fileMetadata,
                image
        );
        fileStorageService.uploadFile(uploadFileDto).subscribe();
    }

    private void deleteImage(Long achievementId) {
        var name = String.format("achievement_%d", achievementId);
        fileStorageService.deleteFile(name).subscribe();
    }
}
