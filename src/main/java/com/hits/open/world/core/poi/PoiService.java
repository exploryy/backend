package com.hits.open.world.core.poi;

import com.hits.open.world.client.poi.PoiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoiService {
    private final PoiClient poiClient;
    private final Map<String, List<PoiEntity>> dataInCity = new ConcurrentHashMap<>();
    private final Random random = new SecureRandom();

    @Async
    public void tryLoadPoiData(String cityName) {
        if (StringUtils.isBlank(cityName) || dataInCity.containsKey(cityName)) {
            return;
        }

        try {
            var poiList = poiClient.getPoiByCityName(cityName);
            dataInCity.put(cityName, poiList);
        } catch (Exception e) {
            log.error("Failed to load POI data for city: {}", cityName, e);
        }
    }

    public PoiEntity getRandomPoiInCity(String cityName) {
        return dataInCity.get(cityName).get(Math.abs(random.nextInt(dataInCity.get(cityName).size())));
    }

    public List<String> getCities() {
        return dataInCity.keySet().stream().toList();
    }
}
