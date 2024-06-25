package com.hits.open.world.core.statistic;

import com.hits.open.world.core.battle_pass.BattlePassService;
import com.hits.open.world.core.buff.BuffService;
import com.hits.open.world.core.buff.repository.enums.BuffStatus;
import com.hits.open.world.core.event.EventService;
import com.hits.open.world.core.event.EventType;
import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.privacy.ClientPrivacyService;
import com.hits.open.world.core.statistic.repository.StatisticEntity;
import com.hits.open.world.core.statistic.repository.StatisticRepository;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.public_interface.event.EventDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.location.LocationStatisticDto;
import com.hits.open.world.public_interface.statistic.TotalStatisticDto;
import com.hits.open.world.public_interface.statistic.UpdateStatisticDto;
import com.hits.open.world.public_interface.user.ProfileDto;
import com.hits.open.world.util.LevelUtil;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.hits.open.world.core.statistic.ExperienceService.*;
import static com.hits.open.world.util.DistanceCalculator.calculateDistanceInMeters;
import static com.hits.open.world.util.LevelUtil.calculateLevel;
import static com.hits.open.world.util.LevelUtil.calculateTotalExperienceInLevel;


@Service
public class StatisticService {
    private final StatisticRepository statisticRepository;
    private final UserService userService;
    private final FriendService friendService;
    private final BattlePassService battlePassService;
    private final EventService eventService;
    private final ClientPrivacyService clientPrivacyService;
    private final BuffService buffService;

    public StatisticService(StatisticRepository statisticRepository, UserService userService, FriendService friendService,
                            BattlePassService battlePassService, EventService eventService, ClientPrivacyService clientPrivacyService,
                            @Lazy BuffService buffService) {
        this.statisticRepository = statisticRepository;
        this.userService = userService;
        this.friendService = friendService;
        this.battlePassService = battlePassService;
        this.eventService = eventService;
        this.clientPrivacyService = clientPrivacyService;
        this.buffService = buffService;
    }

    @Transactional
    public TotalStatisticDto getTopLevel(String userId, int count) {
        List<StatisticEntity> sortedStatistics = statisticRepository.findAllStatisticByExperienceDesc();

        List<LocationStatisticDto> locationStatisticDtos = sortedStatistics.stream()
                .map(statistic -> buildLocationStatisticDto(statistic.clientId()))
                .limit(count)
                .toList();

        int userPosition = findUserPosition(sortedStatistics, userId);

        return new TotalStatisticDto(locationStatisticDtos, userPosition);
    }

    @Transactional
    public TotalStatisticDto getTopDistance(String userId, int count) {
        List<StatisticEntity> sortedStatistics = statisticRepository.findAllStatisticByDistanceDesc();

        List<LocationStatisticDto> locationStatisticDtos = sortedStatistics.stream()
                .map(statistic -> buildLocationStatisticDto(statistic.clientId()))
                .limit(count)
                .toList();

        int userPosition = findUserPosition(sortedStatistics, userId);

        return new TotalStatisticDto(locationStatisticDtos, userPosition);
    }

    @Transactional
    public LocationStatisticDto getUserStatistics(String userId) {
        return buildLocationStatisticDto(userId);
    }

    @Transactional
    public LocationStatisticDto getFriendStatistic(String userId, String friendId) {
        var friends = friendService.getFriends(userId);

        var allFriends = Stream.concat(
                        friends.friends().stream().map(ProfileDto::userId),
                        friends.favoriteFriends().stream().map(ProfileDto::userId)
                ).distinct()
                .toList();

        if (!allFriends.contains(friendId)) {
            throw new ExceptionInApplication("User is not your friend", ExceptionType.NOT_FOUND);
        }

        if (!clientPrivacyService.isPublic(friendId)) {
            return buildPrivateStatisticDto(friendId);
        }

        return buildLocationStatisticDto(friendId);
    }

    @Transactional
    public List<LocationStatisticDto> getLocationsMyFriend(String userId) {
        var friends = friendService.getFriends(userId);

        return Stream.concat(
                        friends.friends().stream().map(ProfileDto::userId),
                        friends.favoriteFriends().stream().map(ProfileDto::userId)
                ).distinct()
                .filter(clientPrivacyService::isPublic)
                .map(this::buildLocationStatisticDto)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tryUpdateStatistic(UpdateStatisticDto updateStatisticDto) {
        Optional<StatisticEntity> optionalStatistic = statisticRepository.findByClientId(updateStatisticDto.userId());

        if (optionalStatistic.isPresent()) {
            updateUserStatistic(updateStatisticDto, optionalStatistic.get());
            return;
        }

        initializeUserStatistic(updateStatisticDto);
    }

    @Transactional
    public void updateExperience(String userId, int addedExperience) {
        var statistic = getUserStatistic(userId);

        int calculatedExperience = calculateExperienceByTask(statistic.experience(), addedExperience);

        var updatedStatistic = updateStatisticEntity(userId, calculatedExperience, statistic);

        handleUserStatistic(statistic, updatedStatistic);
    }

    private void updateUserStatistic(UpdateStatisticDto updateStatisticDto, StatisticEntity statisticEntity) {
        if (shouldUpdateUserMetrics(statisticEntity, updateStatisticDto)) {
            updateUserMetrics(statisticEntity, updateStatisticDto);
            return;
        }

        updateUserPosition(statisticEntity, updateStatisticDto);
    }

    private LocationStatisticDto buildPrivateStatisticDto(String userId) {
        var profile = getUserProfile(userId);

        return new LocationStatisticDto(
                null,
                null,
                null,
                null,
                null,
                null,
                profile
        );
    }

    private boolean shouldUpdateUserMetrics(@NonNull StatisticEntity statisticEntity, @NonNull UpdateStatisticDto updateStatisticDto) {
        return statisticEntity.webSessionId() != null &&
                statisticEntity.webSessionId().equals(updateStatisticDto.webSessionId()) &&
                isCoordinateValid(statisticEntity.previousLatitude()) &&
                isCoordinateValid(statisticEntity.previousLongitude());
    }

    private ProfileDto getUserProfile(String userId) {
        return userService.getProfile(userId);
    }

    private StatisticEntity updateStatisticEntity(String userId, int experience, StatisticEntity statistic) {
        return StatisticEntity.builder()
                .experience(experience)
                .distance(statistic.distance())
                .previousLatitude(statistic.previousLatitude())
                .previousLongitude(statistic.previousLongitude())
                .webSessionId(statistic.webSessionId())
                .lastUpdate(OffsetDateTime.now())
                .clientId(userId)
                .build();
    }

    private StatisticEntity getUserStatistic(String userId) {
        var statistic = statisticRepository.findByClientId(userId);

        if (statistic.isEmpty()) {
            var initialStatistic = buildInitialStatisticEntity(userId);
            return statisticRepository.save(initialStatistic);
        }

        return statistic.get();
    }

    private StatisticEntity buildInitialStatisticEntity(String userId) {
        return StatisticEntity.builder()
                .clientId(userId)
                .lastUpdate(OffsetDateTime.now())
                .build();
    }

    private LocationStatisticDto buildLocationStatisticDto(String userId) {
        var statistic = getUserStatistic(userId);
        var profile = getUserProfile(statistic.clientId());
        int level = calculateLevel(statistic.experience());
        int totalExperienceInLevel = calculateTotalExperienceInLevel(level);

        return new LocationStatisticDto(
                statistic.previousLatitude(),
                statistic.previousLongitude(),
                statistic.experience(),
                statistic.distance(),
                level,
                totalExperienceInLevel,
                profile
        );
    }

    private boolean isCoordinateValid(String coordinate) {
        return coordinate != null && !coordinate.isEmpty();
    }

    private void updateUserMetrics(StatisticEntity statisticEntity, UpdateStatisticDto dto) {
        var userId = statisticEntity.clientId();
        int distanceInMeters = getDistanceInMeters(statisticEntity, dto);
        var coefficient = buffService.getUserBuffs(userId, BuffStatus.EXPERIENCE);
        int calculatedNewExperience = Math.max((int) (calculateNewExperienceByDistance(statisticEntity, dto, distanceInMeters) *
                        coefficient.doubleValue()), 1);

        var updatedStatisticEntity = new StatisticEntity(
                userId,
                calculatedNewExperience + statisticEntity.experience(),
                statisticEntity.distance() + distanceInMeters,
                statisticEntity.webSessionId(),
                dto.latitude().toString(),
                dto.longitude().toString(),
                OffsetDateTime.now()
        );

        handleUserStatistic(statisticEntity, updatedStatisticEntity);
    }

    private void handleUserStatistic(StatisticEntity statisticEntity, StatisticEntity updatedStatisticEntity) {
        var userId = updatedStatisticEntity.clientId();
        statisticRepository.updateStatistic(updatedStatisticEntity);
        eventService.sendEvent(userId, new EventDto(String.valueOf(updatedStatisticEntity.experience()), EventType.UPDATE_EXPERIENCE));
        battlePassService.addExperience(userId, updatedStatisticEntity.experience() - statisticEntity.experience());

        var prevLevel = LevelUtil.calculateLevel(statisticEntity.experience());
        var newLevel = LevelUtil.calculateLevel(updatedStatisticEntity.experience());
        if (prevLevel != newLevel) {
            eventService.sendEvent(userId, new EventDto("%s;%s".formatted(newLevel, LevelUtil.calculateTotalExperienceInLevel(newLevel)), EventType.UPDATE_LEVEL));
        }
    }

    private int getDistanceInMeters(StatisticEntity statisticEntity, UpdateStatisticDto dto) {
        var firstLatitude = dto.latitude().doubleValue();
        var firstLongitude = dto.longitude().doubleValue();

        var secondLatitude = Double.parseDouble(statisticEntity.previousLatitude());
        var secondLongitude = Double.parseDouble(statisticEntity.previousLongitude());

        return calculateDistanceInMeters(firstLatitude, firstLongitude, secondLatitude, secondLongitude);
    }

    private void updateUserPosition(StatisticEntity statisticEntity, UpdateStatisticDto dto) {
        var updatedStatistic = new StatisticEntity(
                statisticEntity.clientId(),
                statisticEntity.experience(),
                statisticEntity.distance(),
                dto.webSessionId(),
                dto.latitude().toString(),
                dto.longitude().toString(),
                OffsetDateTime.now()
        );

        statisticRepository.updateStatistic(updatedStatistic);
    }

    private void initializeUserStatistic(UpdateStatisticDto dto) {
        var statistic = new StatisticEntity(
                dto.userId(),
                0,
                0,
                dto.webSessionId(),
                dto.latitude().toString(),
                dto.longitude().toString(),
                OffsetDateTime.now()
        );

        statisticRepository.save(statistic);
    }

    private int findUserPosition(List<StatisticEntity> profiles, String userId) {
        OptionalInt position = IntStream.range(0, profiles.size())
                .filter(i -> profiles.get(i).clientId().equals(userId))
                .findFirst();

        return position.isPresent() ? position.getAsInt() + 1 : -1;
    }

}
