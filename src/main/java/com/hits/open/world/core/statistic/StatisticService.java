package com.hits.open.world.core.statistic;

import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.statistic.repository.StatisticEntity;
import com.hits.open.world.core.statistic.repository.StatisticRepository;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.core.websocket.WebSocketClient;
import com.hits.open.world.public_interface.statistic.TotalStatisticDto;
import com.hits.open.world.public_interface.statistic.UpdateStatisticDto;
import com.hits.open.world.public_interface.statistic.UserStatisticDto;
import com.hits.open.world.public_interface.user.ProfileDto;
import com.hits.open.world.public_interface.user_location.LocationStatisticDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static com.hits.open.world.core.statistic.ExperienceService.calculateExperienceByDistance;
import static com.hits.open.world.core.statistic.ExperienceService.calculateExperienceByTask;
import static com.hits.open.world.util.DistanceCalculator.calculateDistanceInMeters;
import static com.hits.open.world.util.LevelUtil.calculateLevel;
import static com.hits.open.world.util.LevelUtil.calculateTotalExperienceInLevel;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository statisticRepository;
    private final UserService userService;
    private final FriendService friendService;
    private final WebSocketClient webSocketClient;

    public TotalStatisticDto getTotal(String userId, int count) {
        List<StatisticEntity> allStatistics = statisticRepository.findAllStatistic();

        List<StatisticEntity> sortedStatistics = allStatistics.stream()
                .sorted(Comparator.comparingInt(StatisticEntity::experience).reversed()
                        .thenComparingInt(StatisticEntity::distance).reversed())
                .limit(count)
                .toList();

        List<ProfileDto> profiles = sortedStatistics.stream()
                .map(statisticEntity -> userService.getProfile(statisticEntity.clientId()))
                .toList();


        int userPosition = findUserPosition(profiles, userId);
        return new TotalStatisticDto(profiles, userPosition);
    }

    public List<LocationStatisticDto> getLocationsMyFriend(String userId) {
        var friends = friendService.getFriends(userId);
        return friends.friends().stream()
                .map(friendDto -> getInfo(friendDto.userId()))
                .toList();
    }

    public UserStatisticDto getUserStatistics(String userId) {
        var statistic = getUserStatistic(userId);

        int level = calculateLevel(statistic.experience());
        int totalExperienceInLevel = calculateTotalExperienceInLevel(level);
        return new UserStatisticDto(level, statistic.experience(), statistic.distance(), totalExperienceInLevel);
    }

    public LocationStatisticDto getInfo(String userId) {
        var statistic = getUserStatistic(userId);

        var profile = userService.getProfile(statistic.clientId());

        int level = calculateLevel(statistic.experience());
        return new LocationStatisticDto(profile.username(), profile.email(), profile.userId(), statistic.previousLatitude(),
                statistic.previousLongitude(), statistic.experience(), statistic.distance(), level);
    }

    public void updateExperience(String userId, int addedExperience) {
        var statistic = getUserStatistic(userId);

        int calculatedExperience = calculateExperienceByTask(statistic.experience(), addedExperience);

        var updatedStatistic = StatisticEntity.builder()
                .experience(calculatedExperience)
                .distance(statistic.distance())
                .previousLatitude(statistic.previousLatitude())
                .previousLongitude(statistic.previousLongitude())
                .webSessionId(statistic.webSessionId())
                .lastUpdate(OffsetDateTime.now())
                .clientId(userId)
                .build();

        statisticRepository.updateStatistic(updatedStatistic);
        sendClientInfo(userId, calculatedExperience);
    }

    public void updateStatistic(UpdateStatisticDto dto) {
        var statistic = statisticRepository.findByClientId(dto.userId());

        if (statistic.isPresent()) {
            var statisticEntity = statistic.get();

            if (statisticEntity.webSessionId() != null && statisticEntity.webSessionId().equals(dto.webSessionId()) &&
                    isCoordinateValid(statisticEntity.previousLatitude()) &&
                    isCoordinateValid(statisticEntity.previousLongitude())) {
                updateUserInfo(statisticEntity, dto);
                return;
            }

            updateCoordinates(statisticEntity, dto);
            return;
        }

        initUserStatistic(dto);
    }

    private void sendClientInfo(String userId, int experience) {
        int level = calculateLevel(experience);
        webSocketClient.sendUserExperience(userId, String.valueOf(experience));
        webSocketClient.sendUserLevel(userId, String.valueOf(level));
    }

    private StatisticEntity getUserStatistic(String userId) {
        var statistic = statisticRepository.findByClientId(userId);

        if (statistic.isEmpty()) {
            var initialStatistic = StatisticEntity.builder()
                    .clientId(userId)
                    .lastUpdate(OffsetDateTime.now())
                    .build();
            return statisticRepository.save(initialStatistic);
        }

        return statistic.get();
    }

    private boolean isCoordinateValid(String coordinate) {
        return coordinate != null && !coordinate.isEmpty();
    }

    private void updateUserInfo(StatisticEntity statisticEntity, UpdateStatisticDto dto) {
        int distanceInMeters = calculateDistanceInMeters(dto.latitude().doubleValue(), dto.longitude().doubleValue(),
                new BigDecimal(statisticEntity.previousLatitude()).doubleValue(),
                new BigDecimal(statisticEntity.previousLongitude()).doubleValue());

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
            sendClientInfo(statisticEntity.clientId(), calculatedExperience);
    }

    private void updateCoordinates(StatisticEntity statisticEntity, UpdateStatisticDto dto) {
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

    private void initUserStatistic(UpdateStatisticDto dto) {
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
