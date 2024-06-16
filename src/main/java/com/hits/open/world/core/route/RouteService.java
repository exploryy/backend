package com.hits.open.world.core.route;

import com.hits.open.world.core.route.repository.PointRouteEntity;
import com.hits.open.world.core.route.repository.RouteEntity;
import com.hits.open.world.core.route.repository.RouteRepository;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.route.CreateRouteDto;
import com.hits.open.world.public_interface.route.PointDto;
import com.hits.open.world.public_interface.route.RouteDto;
import com.hits.open.world.util.PointUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

    @Transactional
    public Long createRoute(CreateRouteDto dto) {
        List<PointDto> reversedPoints = dto.points();
        Collections.reverse(reversedPoints);
        routeRepository.savePoints(
                reversedPoints
                        .stream()
                        .map(point -> new PointRouteEntity(
                                point.longitude(),
                                point.latitude(),
                                point.nextLongitude(),
                                point.nextLatitude()
                        ))
                        .toList()
        );
        var routeEntity = new RouteEntity(
                null,
                PointUtil.distanceInMeters(dto.points()),
                dto.startPointLatitude(),
                dto.startPointLongitude()
        );
        var route = routeRepository.saveRoute(routeEntity);
        return route.routeId();
    }

    public RouteDto getRoute(Long routeId) {
        var routeEntity = routeRepository.getRoute(routeId)
                .orElseThrow(() -> new ExceptionInApplication("Route not found", ExceptionType.NOT_FOUND));
        return new RouteDto(
                routeRepository.getPointsInRoute(routeId)
                        .stream()
                        .map(pointRouteEntity -> new PointDto(
                                pointRouteEntity.latitude(),
                                pointRouteEntity.longitude(),
                                pointRouteEntity.nextLatitude(),
                                pointRouteEntity.nextLongitude()
                        ))
                        .toList(),
                routeEntity.distance(),
                routeEntity.routeId()
        );
    }

}
