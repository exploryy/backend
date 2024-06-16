package com.hits.open.world.client.poi;

import com.hits.open.world.core.poi.PoiEntity;

import java.util.List;

public interface PoiClient {
    List<PoiEntity> getPoiByCityName(String cityName);
}
