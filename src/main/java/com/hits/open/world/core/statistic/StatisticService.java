package com.hits.open.world.core.statistic;

import com.google.gson.Gson;
import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.statistic.repository.StatisticEntity;
import com.hits.open.world.core.statistic.repository.StatisticRepository;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.core.websocket.client.WebSocketClient;
import com.hits.open.world.public_interface.friend.FriendDto;
import com.hits.open.world.public_interface.statistic.ExperienceDto;
import com.hits.open.world.public_interface.statistic.LevelDto;
import com.hits.open.world.public_interface.statistic.TotalStatisticDto;
import com.hits.open.world.public_interface.statistic.UpdateStatisticDto;
import com.hits.open.world.public_interface.user.ProfileDto;
import com.hits.open.world.public_interface.location.LocationStatisticDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.hits.open.world.core.statistic.ExperienceService.calculateExperienceByDistance;
import static com.hits.open.world.core.statistic.ExperienceService.calculateExperienceByTask;
import static com.hits.open.world.util.DistanceCalculator.calculateDistanceInMeters;
import static com.hits.open.world.util.LevelUtil.calculateLevel;
import static com.hits.open.world.util.LevelUtil.calculateTotalExperienceInLevel;


@Service
@RequiredArgsConstructor
public class StatisticService {
    private static final Gson mapper = new Gson();
    private final StatisticRepository statisticRepository;
    private final UserService userService;
    private final FriendService friendService;
    private final WebSocketClient webSocketClient;

    @Transactional
    public TotalStatisticDto getTotal(String userId, int count) {
        List<StatisticEntity> allStatistics = statisticRepository.findAllStatistic();

        List<StatisticEntity> sortedStatistics = sortStatistics(allStatistics, count);

        List<ProfileDto> profiles = getProfiles(sortedStatistics);

        int userPosition = findUserPosition(profiles, userId);

        return new TotalStatisticDto(profiles, userPosition);
    }

    @Transactional
    public LocationStatisticDto getUserStatistics(String userId) {
        return buildLocationStatisticDto(userId);
    }

    @Transactional
    public List<LocationStatisticDto> getLocationsMyFriend(String userId) {
        var friends = friendService.getFriends(userId);

        return Stream.concat(
                        friends.friends().stream().map(ProfileDto::userId),
                        friends.favoriteFriends().stream().map(ProfileDto::userId)
                ).distinct()
                .map(this::buildLocationStatisticDto)
                .toList();
    }

    @Transactional
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

        statisticRepository.updateStatistic(updatedStatistic);
        sendEventInfo(userId, calculatedExperience);
    }

    private void updateUserStatistic(UpdateStatisticDto updateStatisticDto, StatisticEntity statisticEntity) {
        if (shouldUpdateUserMetrics(statisticEntity, updateStatisticDto)) {
            updateUserMetrics(statisticEntity, updateStatisticDto);
            return;
        }

        updateUserPosition(statisticEntity, updateStatisticDto);
    }

    private boolean shouldUpdateUserMetrics(@NonNull StatisticEntity statisticEntity, @NonNull UpdateStatisticDto updateStatisticDto) {
        return statisticEntity.webSessionId() != null &&
                statisticEntity.webSessionId().equals(updateStatisticDto.webSessionId()) &&
                isCoordinateValid(statisticEntity.previousLatitude()) &&
                isCoordinateValid(statisticEntity.previousLongitude());
    }

    private List<StatisticEntity> sortStatistics(List<StatisticEntity> statistics, int count) {
        return statistics.stream()
                .sorted(Comparator.comparingInt(StatisticEntity::experience).reversed()
                        .thenComparingInt(StatisticEntity::distance).reversed())
                .limit(count)
                .toList();
    }

    private List<ProfileDto> getProfiles(List<StatisticEntity> statistics) {
        return statistics.stream()
                .map(statisticEntity -> getUserProfile(statisticEntity.clientId()))
                .toList();
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

    private void sendEventInfo(String userId, int experience) {
        sendExperience(userId, experience);
        sendLevel(userId, experience);
    }

    private void sendExperience(String userId, int experience) {
        var experienceDto = new ExperienceDto(experience);
        var response = mapper.toJson(experienceDto);

        webSocketClient.sendEvent(userId, response);
    }

    private void sendLevel(String userId, int experience) {
        int level = calculateLevel(experience);
        var levelDto = new LevelDto(level);

        var response = mapper.toJson(levelDto);

        webSocketClient.sendEvent(userId, response);
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
        int distanceInMeters = getDistanceInMeters(statisticEntity, dto);

        int calculatedExperience = calculateExperienceByDistance(statisticEntity, dto, distanceInMeters);

        var updatedStatistic = new StatisticEntity(
                statisticEntity.clientId(),
                calculatedExperience,
                statisticEntity.distance() + distanceInMeters,
                statisticEntity.webSessionId(),
                dto.latitude().toString(),
                dto.longitude().toString(),
                OffsetDateTime.now()
        );

        statisticRepository.updateStatistic(updatedStatistic);
        sendEventInfo(statisticEntity.clientId(), calculatedExperience);
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

    private int findUserPosition(List<ProfileDto> profiles, String userId) {
        OptionalInt position = IntStream.range(0, profiles.size())
                .filter(i -> profiles.get(i).userId().equals(userId))
                .findFirst();

        return position.isPresent() ? position.getAsInt() + 1 : -1;
    }

}
