package com.hits.open.world.core.location;

import com.hits.open.world.core.coin.CoinService;
import com.hits.open.world.core.location.repository.UserLocationEntity;
import com.hits.open.world.core.location.repository.UserLocationRepository;
import com.hits.open.world.public_interface.user_location.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLocationService {
    private static final double DEGREES_TO_KM_LAT = 111.0;
    private static final double RADIUS_IN_KILOMETERS = 2.0;
    private static final int POINTS_NUMBER = 2;
    private final UserLocationRepository userLocationRepository;
    private final CoinService coinService;

    @Transactional
    public void updateUserLocation(LocationDto locationDto) {
        var savedEntity = getUserLocation(locationDto.clientId());

        if (savedEntity.isPresent() && checkIfLessOneDay(savedEntity.get())) {
            return;
        }

        var entity = UserLocationEntity.builder()
                .longitude(String.valueOf(locationDto.longitude()))
                .latitude(String.valueOf(locationDto.latitude()))
                .lastVisitation(OffsetDateTime.now())
                .clientId(locationDto.clientId())
                .build();

        userLocationRepository.save(entity);

        coinService.deleteAllClientCoins(locationDto.clientId());
        generateNewCoins(locationDto);
    }

    private void generateNewCoins(LocationDto locationDto) {
        for (int i = 0; i < POINTS_NUMBER; i++) {
            double randomOffsetLat = Math.random() * (RADIUS_IN_KILOMETERS / DEGREES_TO_KM_LAT);
            double randomOffsetLon = Math.random() * (RADIUS_IN_KILOMETERS /
                    (DEGREES_TO_KM_LAT * Math.cos(Math.toRadians(locationDto.latitude().doubleValue()))));

            BigDecimal latitude = BigDecimal.valueOf(randomOffsetLat).add(BigDecimal.valueOf(randomOffsetLon));
            BigDecimal longitude = BigDecimal.valueOf(randomOffsetLat).add(BigDecimal.valueOf(randomOffsetLon));
            LocationDto location = new LocationDto(locationDto.clientId(), latitude, longitude);
            coinService.save(location);
        }
    }

    public Optional<UserLocationEntity> getUserLocation(String clientId) {
        return userLocationRepository.findById(clientId);
    }

    private boolean checkIfLessOneDay(UserLocationEntity entity) {
        return entity.lastVisitation().isAfter(OffsetDateTime.now().minusDays(1));
    }

}
