package com.hits.open.world.core.multipolygon.factory.polygon;

import com.hits.open.world.core.multipolygon.enums.FigureType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class PolygonServiceFactory {
    private static final Map<FigureType, PolygonService> polygonServiceMap = new ConcurrentHashMap<>();
    private final List<PolygonService> polygonServices;

    public static PolygonService getPolygonService(FigureType figureType) {
        PolygonService polygonService = polygonServiceMap.get(figureType);

        if (polygonService == null) {
            throw new IllegalArgumentException("Polygon service not found for figure type: " + figureType);
        }

        return polygonService;
    }

    @PostConstruct
    public void init() {
        for (PolygonService polygonService : polygonServices) {
            polygonServiceMap.put(polygonService.getType(), polygonService);
        }
    }

}
