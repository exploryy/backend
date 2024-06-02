package com.hits.open.world.core.location;

import com.hits.open.world.core.coin.CoinService;
import com.hits.open.world.core.friend.repository.NotificationFriendService;
import com.hits.open.world.core.location.repository.UserLocationEntity;
import com.hits.open.world.core.location.repository.UserLocationRepository;
import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.public_interface.user_location.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserLocationService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double RADIUS_IN_KILOMETERS = 1.0;
    private static final int MIN_POINTS_NUMBER = 2;
    private final UserLocationRepository userLocationRepository;
    private final StatisticService statisticService;
    private final NotificationFriendService notificationFriendService;
    private final CoinService coinService;

    @Transactional
    public void updateUserLocation(LocationDto locationDto) {
        var savedEntity = getUserLocation(locationDto.clientId());

        var entity = UserLocationEntity.builder()
                .longitude(String.valueOf(locationDto.longitude()))
                .latitude(String.valueOf(locationDto.latitude()))
                .lastVisitation(OffsetDateTime.now())
                .clientId(locationDto.clientId())
                .build();

        notificationFriendService.notifyFriendsAboutNewLocation(locationDto);

        if (savedEntity.isPresent() && checkIfLessOneDay(savedEntity.get())) {
            userLocationRepository.update(entity);
            return;
        }

        if (savedEntity.isPresent()) {
            userLocationRepository.update(entity);
        } else {
            userLocationRepository.save(entity);
        }

        coinService.deleteAllClientCoins(locationDto.clientId());
        generateNewCoins(locationDto);
    }

    private void generateNewCoins(LocationDto locationDto) {
        var userStatistic = statisticService.getUserStatistics(locationDto.clientId());
        int userLevel = userStatistic.level();
        int coinsCount = Math.max(userLevel, MIN_POINTS_NUMBER);

        for (int i = 0; i < coinsCount; i++) {
            LocationDto newLocation = generateRandomLocation(locationDto);
            coinService.save(newLocation);
        }
    }

    public Optional<UserLocationEntity> getUserLocation(String clientId) {
        return userLocationRepository.findById(clientId);
    }

    private boolean checkIfLessOneDay(UserLocationEntity entity) {
        return entity.lastVisitation().isAfter(OffsetDateTime.now().minusDays(1));
    }

    private LocationDto generateRandomLocation(LocationDto baseLocation) {
        double lat = baseLocation.latitude().doubleValue();
        double lon = baseLocation.longitude().doubleValue();

        double angle = secureRandom.nextDouble() * 2 * Math.PI;
        double distance = secureRandom.nextDouble() * RADIUS_IN_KILOMETERS;

        double deltaLat = distance / EARTH_RADIUS_KM * (180 / Math.PI);
        double deltaLon = distance / (EARTH_RADIUS_KM * Math.cos(Math.toRadians(lat))) * (180 / Math.PI);

        double newLat = lat + deltaLat * Math.cos(angle);
        double newLon = lon + deltaLon * Math.sin(angle);

        BigDecimal latitude = BigDecimal.valueOf(newLat).setScale(20, BigDecimal.ROUND_HALF_UP);
        BigDecimal longitude = BigDecimal.valueOf(newLon).setScale(20, BigDecimal.ROUND_HALF_UP);

        return new LocationDto(baseLocation.clientId(), latitude, longitude);
    }

}
