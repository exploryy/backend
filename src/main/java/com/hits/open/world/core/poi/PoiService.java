package com.hits.open.world.core.poi;

import com.hits.open.world.client.poi.PoiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoiService {
    private final PoiClient poiClient;
    private final Map<String, List<PoiEntity>> dataInCity = new HashMap<>();

    @Async
    public void tryLoadPoiData(String cityName) {
        if (dataInCity.containsKey(cityName)) {
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
        return dataInCity.get(cityName).get((int) (Math.random() * dataInCity.get(cityName).size()));
    }

    public List<String> getCities() {
        return dataInCity.keySet().stream().toList();
    }
}
