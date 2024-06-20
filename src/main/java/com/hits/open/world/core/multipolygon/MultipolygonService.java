package com.hits.open.world.core.multipolygon;

import com.hits.open.world.client.polygon.PolygonClient;
import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.multipolygon.enums.FigureType;
import com.hits.open.world.core.multipolygon.factory.polygon.PolygonService;
import com.hits.open.world.core.multipolygon.repository.MultipolygonRepository;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.multipolygon.AreaDtoResponse;
import com.hits.open.world.public_interface.multipolygon.PolygonRequestDto;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;
import com.hits.open.world.public_interface.multipolygon.geo.MultipolygonGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static com.hits.open.world.core.multipolygon.factory.geo.GeoDtoFactory.buildMultiPolygonGeoDto;
import static com.hits.open.world.core.multipolygon.factory.geo.GeoDtoFactory.buildPolygonGeoDto;
import static com.hits.open.world.core.multipolygon.factory.polygon.PolygonServiceFactory.getPolygonService;

@Service
@RequiredArgsConstructor
public class MultipolygonService {
    private final MultipolygonRepository multipolygonRepository;
    private final FriendService friendService;
    private final PolygonClient polygonClient;

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

    public BigDecimal calculatePercentArea(PolygonRequestDto requestDto) {
        var placeName = requestDto.createPolygonRequestDto().place();

        if (StringUtils.isBlank(placeName)) {
            return null;
        }

        var multiPolygon = multipolygonRepository.getAllCoordinates(placeName);

        if (multiPolygon == null) {
            var osmId = polygonClient.getNominatimData(placeName);
            var multiPolygons = polygonClient.getPolygonData(osmId);
            Polygon polygon = buildPolygon(multiPolygons);
            multipolygonRepository.insert(placeName, polygon);
        }

        return multipolygonRepository.calculatePercentArea(requestDto.userId(), placeName);
    }

    public boolean isNewTerritory(PolygonRequestDto requestDto) {
        var longitude = requestDto.createPolygonRequestDto().longitude().doubleValue();
        var latitude = requestDto.createPolygonRequestDto().latitude().doubleValue();

        var coordinate = new Coordinate(longitude, latitude);

        var point = new GeometryFactory().createPoint(coordinate);
        return multipolygonRepository.isPointInPolygon(point, requestDto.userId());
    }

    public GeoDto getAllPolygonsFriend(String userId, String friendId) {
        var friends = friendService.getFriends(userId);
        var allFriends = Stream.concat(
                friends.friends().stream(),
                friends.favoriteFriends().stream()
        ).distinct().toList();

        boolean isFriend = allFriends.stream().anyMatch(friendDto -> friendDto.userId().equals(friendId));

        if (!isFriend) {
            throw new ExceptionInApplication("You don't have a friend", ExceptionType.INVALID);
        }

        return getAllPolygons(friendId);
    }

    public void save(PolygonRequestDto requestDto) {
        var coordinate = new Coordinate(requestDto.createPolygonRequestDto().longitude().doubleValue(),
                requestDto.createPolygonRequestDto().latitude().doubleValue());
        var point = new GeometryFactory().createPoint(coordinate);
        var polygon = buildPolygon(point, requestDto.createPolygonRequestDto().figureType());

        multipolygonRepository.insert(requestDto.userId(), polygon);

        var changedPolygon = multipolygonRepository.getPolygonByPoint(point, requestDto.userId());
        buildPolygonGeoDto(changedPolygon);
    }

    private Polygon buildPolygon(Point centralPoint, FigureType type) {
        PolygonService polygonService = getPolygonService(type);
        return polygonService.constructPolygon(centralPoint);
    }

    private Polygon buildPolygon(MultipolygonGeometry geometry) {
        List<List<Double>> coordinates = geometry.coordinates().getFirst().getFirst();
        Coordinate[] jtsCoordinates = new Coordinate[coordinates.size()];

        for (int i = 0; i < coordinates.size(); i++) {
            List<Double> coordinate = coordinates.get(i);
            double lon = coordinate.get(0);
            double lat = coordinate.get(1);
            jtsCoordinates[i] = new Coordinate(lon, lat);
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing linearRing = geometryFactory.createLinearRing(jtsCoordinates);
        return geometryFactory.createPolygon(linearRing);
    }

}
