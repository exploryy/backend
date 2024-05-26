package com.hits.open.world.core.multipolygon;

import com.hits.open.world.core.multipolygon.factory.polygon.PolygonService;
import com.hits.open.world.core.multipolygon.repository.MultipolygonRepository;
import com.hits.open.world.public_interface.multipolygon.AreaDtoResponse;
import com.hits.open.world.public_interface.multipolygon.CreatePolygonRequestDto;
import com.hits.open.world.core.multipolygon.enums.FigureType;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;
import com.vividsolutions.jts.geom.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.hits.open.world.core.multipolygon.factory.geo.GeoDtoFactory.buildMultiPolygonGeoDto;
import static com.hits.open.world.core.multipolygon.factory.geo.GeoDtoFactory.buildPolygonGeoDto;
import static com.hits.open.world.core.multipolygon.factory.polygon.PolygonServiceFactory.getPolygonService;

@Service
@RequiredArgsConstructor
public class MultipolygonService {
    private static final String TOMSK_ID = "tomsk";
    private final MultipolygonRepository multipolygonRepository;

    public GeoDto getAllPolygons(String userId) {
        var geoString = multipolygonRepository.getAllCoordinates(userId);
        return buildMultiPolygonGeoDto(geoString);
    }

    public void delete(String userId) {
        multipolygonRepository.delete(userId);
    }

    public AreaDtoResponse calculateArea(String userId) {
        var area = multipolygonRepository.calculateArea(userId);
        return new AreaDtoResponse(area);
    }

    public BigDecimal calculatePercentArea(String userId) {
        BigDecimal userArea = multipolygonRepository.calculateArea(userId);
        BigDecimal tomskArea = multipolygonRepository.calculateArea(TOMSK_ID);
        return userArea.divide(tomskArea, 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    public GeoDto save(CreatePolygonRequestDto createPolygonRequestDto, String userId) {
        var coordinate = new Coordinate(createPolygonRequestDto.longitude().doubleValue(), createPolygonRequestDto.latitude().doubleValue());
        var point = new GeometryFactory().createPoint(coordinate);
        var polygon = buildPolygon(point, createPolygonRequestDto.figureType());

        multipolygonRepository.insert(userId, polygon);

        var changedPolygon = multipolygonRepository.getPolygonByPoint(point, userId);
        return buildPolygonGeoDto(changedPolygon);
    }

    private Polygon buildPolygon(Point centralPoint, FigureType type) {
        PolygonService polygonService = getPolygonService(type);
        return polygonService.constructPolygon(centralPoint);
    }

}
