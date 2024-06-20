package com.hits.open.world.core.location;

import com.hits.open.world.core.buff.BuffService;
import com.hits.open.world.core.buff.repository.enums.BuffStatus;
import com.hits.open.world.core.coin.CoinService;
import com.hits.open.world.core.friend.repository.NotificationFriendService;
import com.hits.open.world.core.location.repository.UserLocationEntity;
import com.hits.open.world.core.location.repository.UserLocationRepository;
import com.hits.open.world.core.multipolygon.MultipolygonService;
import com.hits.open.world.core.poi.PoiService;
import com.hits.open.world.core.quest.QuestService;
import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.public_interface.location.LocationDto;
import com.hits.open.world.public_interface.multipolygon.PolygonRequestDto;
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
    private final NotificationFriendService notificationFriendService;
    private final CoinService coinService;
    private final QuestService questService;
    private final PoiService poiService;
    private final MultipolygonService multipolygonService;
    private final BuffService buffService;

    @Transactional
    public void updateUserLocation(PolygonRequestDto requestDto) {
        var savedEntity = getUserLocation(requestDto.userId());

        var userLocationEntity = buildEntity(requestDto);

        notificationFriendService.notifyFriendsAboutNewLocation(requestDto);
        questService.tryFinishActiveQuests(requestDto);
        poiService.tryLoadPoiData(requestDto.createPolygonRequestDto().place());

        if (multipolygonService.isNewTerritory(requestDto)) {
            questService.tryNotifyUserAboutNewQuest(requestDto);
        }

        if (savedEntity.isPresent() && checkIfLessOneDay(savedEntity.get())) {
            userLocationRepository.update(userLocationEntity);
            return;
        }

        if (savedEntity.isPresent()) {
            userLocationRepository.update(userLocationEntity);
        } else {
            userLocationRepository.initLocation(userLocationEntity);
        }

        coinService.deleteAllClientCoins(requestDto.userId());
        generateNewCoins(requestDto);
    }

    private Optional<UserLocationEntity> getUserLocation(String clientId) {
        return userLocationRepository.findById(clientId);
    }

    private boolean checkIfLessOneDay(UserLocationEntity entity) {
        return entity.lastVisitation().isAfter(OffsetDateTime.now().minusDays(1));
    }

    private UserLocationEntity buildEntity(PolygonRequestDto requestDto) {
        return UserLocationEntity.builder()
                .longitude(String.valueOf(requestDto.createPolygonRequestDto().longitude()))
                .latitude(String.valueOf(requestDto.createPolygonRequestDto().latitude()))
                .lastVisitation(OffsetDateTime.now())
                .clientId(requestDto.userId())
                .build();
    }

    private void generateNewCoins(PolygonRequestDto requestDto) {
        int coinsCount = MIN_POINTS_NUMBER + buffService.getUserBuffs(requestDto.userId(), BuffStatus.COINS).intValue();

        for (int i = 0; i < coinsCount; i++) {
            LocationDto newLocation = generateRandomLocation(requestDto);
            coinService.save(newLocation);
        }
    }

    private LocationDto generateRandomLocation(PolygonRequestDto dto) {
        double lat = dto.createPolygonRequestDto().latitude().doubleValue();
        double lon = dto.createPolygonRequestDto().longitude().doubleValue();

        double angle = secureRandom.nextDouble() * 2 * Math.PI;
        double distance = secureRandom.nextDouble() * RADIUS_IN_KILOMETERS;

        double deltaLat = distance / EARTH_RADIUS_KM * (180 / Math.PI);
        double deltaLon = distance / (EARTH_RADIUS_KM * Math.cos(Math.toRadians(lat))) * (180 / Math.PI);

        double newLat = lat + deltaLat * Math.cos(angle);
        double newLon = lon + deltaLon * Math.sin(angle);

        BigDecimal latitude = BigDecimal.valueOf(newLat).setScale(20, BigDecimal.ROUND_HALF_UP);
        BigDecimal longitude = BigDecimal.valueOf(newLon).setScale(20, BigDecimal.ROUND_HALF_UP);

        return new LocationDto(dto.userId(), latitude, longitude);
    }

}
