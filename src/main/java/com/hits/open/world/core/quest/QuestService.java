package com.hits.open.world.core.quest;

import com.hits.open.world.core.event.EventService;
import com.hits.open.world.core.event.EventType;
import com.hits.open.world.core.file.FileMetadata;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.core.money.MoneyService;
import com.hits.open.world.core.multipolygon.repository.MultipolygonRepository;
import com.hits.open.world.core.quest.repository.QuestRepository;
import com.hits.open.world.core.quest.repository.entity.PointEntity;
import com.hits.open.world.core.quest.repository.entity.pass_quest.PassQuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.QuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestPhotoEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.core.quest.repository.entity.quest.distance.DistanceQuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.point_to_point.PointToPointQuestEntity;
import com.hits.open.world.core.quest.repository.entity.review.QuestReviewEntity;
import com.hits.open.world.core.quest.repository.entity.review.ReviewPhotoEntity;
import com.hits.open.world.core.route.RouteService;
import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.public_interface.event.EventDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.file.UploadFileDto;
import com.hits.open.world.public_interface.multipolygon.PolygonRequestDto;
import com.hits.open.world.public_interface.quest.AllQuestDto;
import com.hits.open.world.public_interface.quest.CommonQuestDto;
import com.hits.open.world.public_interface.quest.CompletedQuestDto;
import com.hits.open.world.public_interface.quest.CreateDistanceQuestDto;
import com.hits.open.world.public_interface.quest.CreatePointToPointQuestDto;
import com.hits.open.world.public_interface.quest.CreateQuestDto;
import com.hits.open.world.public_interface.quest.DistanceQuestDto;
import com.hits.open.world.public_interface.quest.GetQuestsDto;
import com.hits.open.world.public_interface.quest.PointToPointQuestDto;
import com.hits.open.world.public_interface.quest.StartQuestDto;
import com.hits.open.world.public_interface.quest.UpdateQuestDto;
import com.hits.open.world.public_interface.quest.review.AddImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.CreateQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.FullQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.QuestReviewDto;
import com.hits.open.world.public_interface.quest.review.UpdateQuestReviewDto;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.hits.open.world.util.DistanceCalculator.calculateDistanceInMeters;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final RouteService routeService;
    private final StatisticService statisticService;
    private final FileStorageService fileStorageService;
    private final MultipolygonRepository multipolygonRepository;
    private final EventService eventService;
    private final MoneyService moneyService;
    private final UserService userService;

    private Long createQuest(CreateQuestDto dto) {
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
        return questInDb.questId();
    }

    @Transactional
    public void createPointToPointQuest(CreatePointToPointQuestDto dto) {
        var questId = createQuest(dto.questDto());
        var routeId = routeService.createRoute(dto.routeDto());
        var pointToPointQuestEntity = new PointToPointQuestEntity(
                questId,
                routeId
        );
        questRepository.createPointToPointQuest(pointToPointQuestEntity);
    }

    @Transactional
    public void createDistanceQuest(CreateDistanceQuestDto dto) {
        var questId = createQuest(dto.questDto());
        var distanceQuestEntity = new DistanceQuestEntity(
                questId,
                dto.distance(),
                dto.longitude(),
                dto.latitude()
        );
        questRepository.createDistanceQuest(distanceQuestEntity);
    }

    public AllQuestDto getQuests(GetQuestsDto dto) {
        var activeQuests = getMyActiveQuests(dto.userId());
        var completedQuests = getMyCompletedQuests(dto.userId());
        var allQuests = questRepository.getQuestsByName(dto.name())
                .stream()
                .filter(quest -> inOpenArea(quest, dto.userId()))
                .map(this::toDto)
                .collect(groupingBy(quest -> {
                    if (activeQuests.stream().anyMatch(activeQuest -> activeQuest.questId().equals(quest.questId()))) {
                        return QuestStatus.ACTIVE;
                    } else if (completedQuests.stream().anyMatch(completedQuest -> completedQuest.questId().equals(quest.questId()))) {
                        return QuestStatus.COMPLETED;
                    } else {
                        return QuestStatus.NOT_COMPLETED;
                    }
                }));

        return new AllQuestDto(
                allQuests.getOrDefault(QuestStatus.NOT_COMPLETED, List.of()),
                allQuests.getOrDefault(QuestStatus.ACTIVE, List.of()),
                allQuests.getOrDefault(QuestStatus.COMPLETED, List.of())
                        .stream()
                        .map(quest -> toDto(quest, dto.userId()))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<CommonQuestDto> getAllQuest() {
        return questRepository.getQuestsByName("")
                .stream()
                .map(this::toDto)
                .toList();
    }

    private boolean inOpenArea(QuestEntity dto, String userId) {
        var cord = getCoordinates(dto);
        var coordinate = new Coordinate(Double.parseDouble(cord.longitude()), Double.parseDouble(cord.latitude()));
        var point = new GeometryFactory().createPoint(coordinate);
        return multipolygonRepository.isPointInPolygon(point, userId);
    }

    public List<CompletedQuestDto> getMyCompletedQuests(String userId) {
        return questRepository.getFinishedQuests(userId)
                .stream()
                .map(this::toDto)
                .map(quest -> toDto(quest, userId))
                .toList();
    }

    public List<CommonQuestDto> getMyActiveQuests(String userId) {
        return questRepository.getActiveQuests(userId)
                .stream()
                .map(this::toDto)
                .toList();
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

    @Transactional
    public void startQuest(StartQuestDto dto) {
        if (!getMyActiveQuests(dto.userId()).isEmpty()) {
            throw new ExceptionInApplication("You already have active quest", ExceptionType.ALREADY_EXISTS);
        }
        if (questRepository.isQuestFinished(dto.questId(), dto.userId())) {
            throw new ExceptionInApplication("Quest already finished", ExceptionType.ALREADY_EXISTS);
        }
        if (questRepository.isQuestStarted(dto.questId(), dto.userId())) {
            throw new ExceptionInApplication("Quest already started", ExceptionType.ALREADY_EXISTS);
        }

        var passQuestEntity = new PassQuestEntity(
                null,
                dto.questId(),
                null,
                dto.userId(),
                TransportType.valueOf(dto.transportType()),
                LocalDateTime.now(),
                null
        );
        questRepository.startQuest(passQuestEntity);
    }

    public void finishQuest(Long questId, String userId) {
        var passQuest = questRepository.getPassQuestById(userId, questId)
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));
        if (!passQuest.userId().equals(userId)) {
            throw new ExceptionInApplication("It's not your quest", ExceptionType.FORBIDDEN);
        }

        var quest = questRepository.getQuestById(questId)
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));

        var addedExperience = getExperienceForQuest(quest.difficultyType());
        statisticService.updateExperience(userId, addedExperience);
        var addedMoney = getMoneyForQuest(quest.difficultyType());
        moneyService.addMoney(userId, addedMoney);

        var updatedEntity = new PassQuestEntity(
                passQuest.passQuestId(),
                passQuest.questId(),
                passQuest.routeId(),
                passQuest.userId(),
                passQuest.transportType(),
                passQuest.startTime(),
                LocalDateTime.now()
        );
        questRepository.updatePassQuest(updatedEntity);

        var eventDto = new EventDto(
                "%s;%s;%s;%s".formatted(quest.name(), addedExperience, addedMoney, quest.questId()),
                EventType.COMPLETE_QUEST
        );
        eventService.sendEvent(userId, eventDto);
    }

    @Transactional
    public void tryNotifyUserAboutNewQuest(PolygonRequestDto userLocation) {
        var questInOpenArea = questRepository.getQuestsByName("")
                .stream()
                .filter(questEntity -> {
                    switch (questEntity.questType()) {
                        case POINT_TO_POINT -> {
                            var pointToPointQuest = questRepository.getPointToPointQuestByQuestId(questEntity.questId())
                                    .orElseThrow(() -> new ExceptionInApplication("Point to point quest not found", ExceptionType.NOT_FOUND));
                            var way = routeService.getRoute(pointToPointQuest.routeId());
                            var firstPoint = way.points().get(0);
                            return calculateDistanceInMeters(
                                    Double.parseDouble(firstPoint.latitude()),
                                    Double.parseDouble(firstPoint.longitude()),
                                    userLocation.createPolygonRequestDto().latitude().doubleValue(),
                                    userLocation.createPolygonRequestDto().longitude().doubleValue()) <= 100;
                        }
                        case DISTANCE -> {
                            var distanceQuest = questRepository.getDistanceQuestByQuestId(questEntity.questId())
                                    .orElseThrow(() -> new ExceptionInApplication("Distance quest not found", ExceptionType.NOT_FOUND));
                            return calculateDistanceInMeters(
                                    Double.parseDouble(distanceQuest.latitude()),
                                    Double.parseDouble(distanceQuest.longitude()),
                                    userLocation.createPolygonRequestDto().latitude().doubleValue(),
                                    userLocation.createPolygonRequestDto().longitude().doubleValue()) <= 100;
                        }
                        default -> {
                            return false;
                        }
                    }
                }).count();
        if (questInOpenArea > 0) {
            var eventDto = new EventDto(
                    "У вас есть квесты поблизости",
                    EventType.NEW_QUEST
            );
            eventService.sendEvent(userLocation.userId(), eventDto);
        }
    }

    @Transactional
    public void tryFinishActiveQuests(PolygonRequestDto requestDto) {
        var activeQuests = questRepository.getActiveQuests(requestDto.userId());
        for (var quest : activeQuests) {
            switch (quest.questType()) {
                case POINT_TO_POINT -> {
                    final int distance = 50;
                    var pointToPointQuest = questRepository.getPointToPointQuestByQuestId(quest.questId())
                            .orElseThrow(() -> new ExceptionInApplication("Point to point quest not found", ExceptionType.NOT_FOUND));
                    var way = routeService.getRoute(pointToPointQuest.routeId());
                    var lastPoint = way.points().get(way.points().size() - 1);
                    if (calculateDistanceInMeters(
                            Double.parseDouble(lastPoint.latitude()),
                            Double.parseDouble(lastPoint.longitude()),
                            requestDto.createPolygonRequestDto().latitude().doubleValue(),
                            requestDto.createPolygonRequestDto().longitude().doubleValue())
                            <= distance) {
                        finishQuest(quest.questId(), requestDto.userId());
                    }
                }
                case DISTANCE -> {
                    var distanceQuest = questRepository.getDistanceQuestByQuestId(quest.questId())
                            .orElseThrow(() -> new ExceptionInApplication("Distance quest not found", ExceptionType.NOT_FOUND));
                    if (calculateDistanceInMeters(
                            Double.parseDouble(distanceQuest.latitude()),
                            Double.parseDouble(distanceQuest.longitude()),
                            requestDto.createPolygonRequestDto().latitude().doubleValue(),
                            requestDto.createPolygonRequestDto().longitude().doubleValue())
                            >= distanceQuest.routeDistance()) {
                        finishQuest(quest.questId(), requestDto.userId());
                    }
                }
            }
        }
    }

    @Transactional
    public void cancelQuest(Long questId, String userId) {
        var passQuest = questRepository.getPassQuestById(userId, questId)
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));
        if (!passQuest.userId().equals(userId)) {
            throw new ExceptionInApplication("It's not your quest", ExceptionType.FORBIDDEN);
        }

        questRepository.deletePassQuest(passQuest.passQuestId());
    }

    public List<QuestReviewDto> getQuestReviews(Long questId) {
        return questRepository.getQuestReviewsByQuestId(questId)
                .stream()
                .map(review -> {
                    var images = questRepository.getReviewPhotosByReviewId(review.questReviewId())
                            .parallelStream()
                            .map(photoEntity -> "quest_review_%d_photo_%d".formatted(review.questReviewId(), photoEntity.reviewPhotoId()))
                            .map(fileStorageService::getDownloadLinkByName)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                    return new QuestReviewDto(
                            review.questReviewId(),
                            review.score(),
                            review.message(),
                            review.questId(),
                            images,
                            userService.getProfile(review.clientId()),
                            review.createdAt()
                    );
                })
                .toList();
    }

    public PointToPointQuestDto getPointToPointQuest(Long questId) {
        var quest = questRepository.getQuestById(questId)
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));
        var pointToPointQuest = questRepository.getPointToPointQuestByQuestId(questId)
                .orElseThrow(() -> new ExceptionInApplication("Point to point quest not found", ExceptionType.NOT_FOUND));

        var reviews = getQuestReviews(questId);
        return new PointToPointQuestDto(
                toDto(quest),
                routeService.getRoute(pointToPointQuest.routeId()),
                new FullQuestReviewDto(
                        reviews.stream().map(QuestReviewDto::score).mapToInt(Integer::intValue).average().orElse(0),
                        reviews
                )
        );
    }

    public DistanceQuestDto getDistanceQuest(Long questId) {
        var quest = questRepository.getQuestById(questId)
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));
        var distanceQuest = questRepository.getDistanceQuestByQuestId(questId)
                .orElseThrow(() -> new ExceptionInApplication("Distance quest not found", ExceptionType.NOT_FOUND));

        var reviews = getQuestReviews(questId);
        return new DistanceQuestDto(
                toDto(quest),
                distanceQuest.routeDistance(),
                distanceQuest.longitude(),
                distanceQuest.latitude(),
                new FullQuestReviewDto(
                        reviews.stream().map(QuestReviewDto::score).mapToInt(Integer::intValue).average().orElse(0),
                        reviews
                )
        );
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
                dto.questId(),
                OffsetDateTime.now()
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
                Optional.ofNullable(dto.message().orElse(questReview.message().orElse(null))),
                questReview.clientId(),
                questReview.questId(),
                questReview.createdAt()
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

    private CommonQuestDto toDto(QuestEntity entity) {
        var photos = questRepository.getQuestPhotosByQuestId(entity.questId())
                .parallelStream()
                .map(photoEntity -> "quest_%d_photo_%d".formatted(entity.questId(), photoEntity.questPhotoId()))
                .map(fileStorageService::getDownloadLinkByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        var cord = getCoordinates(entity);
        return new CommonQuestDto(
                entity.questId(),
                entity.name(),
                entity.description(),
                entity.difficultyType(),
                entity.questType(),
                entity.transportType(),
                cord.longitude(),
                cord.latitude(),
                photos
        );
    }

    private PointEntity getCoordinates(QuestEntity entity) {
        switch (entity.questType()) {
            case POINT_TO_POINT -> {
                var pointToPointQuest = questRepository.getPointToPointQuestByQuestId(entity.questId())
                        .orElseThrow(() -> new ExceptionInApplication("Point to point quest not found", ExceptionType.NOT_FOUND));
                var route = routeService.getRoute(pointToPointQuest.routeId());
                return new PointEntity(route.points().get(0).longitude(), route.points().get(0).latitude());
            }
            case DISTANCE -> {
                var distanceQuest = questRepository.getDistanceQuestByQuestId(entity.questId())
                        .orElseThrow(() -> new ExceptionInApplication("Distance quest not found", ExceptionType.NOT_FOUND));
                return new PointEntity(distanceQuest.longitude(), distanceQuest.latitude());
            }
            default -> throw new ExceptionInApplication("Quest type not found", ExceptionType.NOT_FOUND);
        }
    }

    private CompletedQuestDto toDto(CommonQuestDto dto, String userId) {
        var completedQuest = questRepository.getPassQuestById(userId, dto.questId())
                .orElseThrow(() -> new ExceptionInApplication("Quest not found", ExceptionType.NOT_FOUND));
        return new CompletedQuestDto(
                dto.questId(),
                dto.name(),
                dto.description(),
                dto.difficultyType(),
                dto.questType(),
                dto.transportType(),
                dto.longitude(),
                dto.latitude(),
                dto.images(),
                completedQuest.startTime(),
                completedQuest.endTime()
        );
    }

    private int getMoneyForQuest(DifficultyType type) {
        switch (type) {
            case EASY -> {
                return 100;
            }
            case MEDIUM -> {
                return 500;
            }
            case HARD -> {
                return 1200;
            }
            default -> throw new ExceptionInApplication("Invalid difficulty type", ExceptionType.INVALID);
        }
    }

    private int getExperienceForQuest(DifficultyType type) {
        switch (type) {
            case EASY -> {
                return 300;
            }
            case MEDIUM -> {
                return 600;
            }
            case HARD -> {
                return 2000;
            }
            default -> throw new ExceptionInApplication("Invalid difficulty type", ExceptionType.INVALID);
        }
    }
}