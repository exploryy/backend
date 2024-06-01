package com.hits.open.world.core.statistic;

import com.hits.open.world.core.statistic.repository.StatisticEntity;
import com.hits.open.world.core.statistic.repository.StatisticRepository;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.public_interface.statistic.TotalStatisticDto;
import com.hits.open.world.public_interface.statistic.UserStatisticDto;
import com.hits.open.world.public_interface.user.ProfileDto;
import com.hits.open.world.public_interface.user_location.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static com.hits.open.world.util.BuffCalculator.calculateExperience;
import static com.hits.open.world.util.DistanceCalculator.calculateDistanceInMeters;
import static com.hits.open.world.util.LevelUtil.calculateLevel;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository statisticRepository;
    private final UserService userService;

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

    public UserStatisticDto getUserStatistics(String userId) {
        var statistic = getUserStatistic(userId);

        int level = calculateLevel(statistic.experience());
        return new UserStatisticDto(level, statistic.experience(), statistic.distance());
    }

    public void updateExperience(String userId, int addedExperience) {
        var statistic = getUserStatistic(userId);

        int calculatedExperience = calculateExperience(statistic.experience(), addedExperience);

        var updatedStatistic = StatisticEntity.builder()
                .experience(calculatedExperience)
                .distance(statistic.distance())
                .previousLatitude(statistic.previousLatitude())
                .previousLongitude(statistic.previousLongitude())
                .webSessionId(statistic.webSessionId())
                .clientId(userId)
                .build();

        statisticRepository.updateStatistic(updatedStatistic);
    }

    public void updateStatistic(String webSocketSessionId, LocationDto dto) {
        var statistic = statisticRepository.findByClientId(dto.clientId());

        if (statistic.isPresent()) {
            var statisticEntity = statistic.get();

            if (statisticEntity.webSessionId() != null && statisticEntity.webSessionId().equals(webSocketSessionId) &&
                    isCoordinateValid(statisticEntity.previousLatitude()) &&
                    isCoordinateValid(statisticEntity.previousLongitude())) {
                updateDistance(statisticEntity, dto);
                return;
            }

            updateCoordinates(statisticEntity, dto, webSocketSessionId);
            return;
        }

        initUserStatistic(dto, webSocketSessionId);
    }

    private StatisticEntity getUserStatistic(String userId) {
        var statistic = statisticRepository.findByClientId(userId);

        if (statistic.isEmpty()) {
            var initialStatistic = StatisticEntity.builder()
                    .clientId(userId)
                    .build();
            return statisticRepository.save(initialStatistic);
        }

        return statistic.get();
    }

    private boolean isCoordinateValid(String coordinate) {
        return coordinate != null && !coordinate.isEmpty();
    }

    private void updateDistance(StatisticEntity statisticEntity, LocationDto dto) {
        int distance = calculateDistanceInMeters(dto.latitude().doubleValue(), dto.longitude().doubleValue(),
                new BigDecimal(statisticEntity.previousLatitude()).doubleValue(),
                new BigDecimal(statisticEntity.previousLongitude()).doubleValue());

        int calculatedExperience = calculateExperience(statisticEntity.experience(), distance);

        var updatedStatistic = new StatisticEntity(
                statisticEntity.clientId(),
                calculatedExperience,
                statisticEntity.distance() + distance,
                statisticEntity.webSessionId(),
                dto.latitude().toString(),
                dto.longitude().toString()
        );

        statisticRepository.updateStatistic(updatedStatistic);
    }

    private void updateCoordinates(StatisticEntity statisticEntity, LocationDto dto, String webSocketSessionId) {
        var updatedStatistic = new StatisticEntity(
                statisticEntity.clientId(),
                statisticEntity.experience(),
                statisticEntity.distance(),
                webSocketSessionId,
                dto.latitude().toString(),
                dto.longitude().toString()
        );

        statisticRepository.updateStatistic(updatedStatistic);
    }

    private void initUserStatistic(LocationDto dto, String webSocketSessionId) {
        var statistic = new StatisticEntity(
                dto.clientId(),
                0,
                0,
                webSocketSessionId,
                dto.latitude().toString(),
                dto.longitude().toString()
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
