package com.hits.open.world.core.route;

import com.hits.open.world.core.route.repository.PointEntity;
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

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

    @Transactional
    public Long createRoute(CreateRouteDto dto) {
        var routeEntity = new RouteEntity(
                null,
                PointUtil.distanceInMeters(dto.points()),
                dto.points().stream()
                        .map(pointDto -> new PointEntity(
                                pointDto.latitude(),
                                pointDto.longitude(),
                                null
                        ))
                        .toList()
        );
        var route = routeRepository.saveRoute(routeEntity);
        return route.routeId();
    }

    public RouteDto getRoute(Long routeId) {
        var routeEntity = routeRepository.getRoute(routeId)
                .orElseThrow(() -> new ExceptionInApplication("Route not found", ExceptionType.NOT_FOUND));
        return new RouteDto(
                routeEntity.points().stream()
                        .map(pointEntity -> new PointDto(
                                pointEntity.latitude(),
                                pointEntity.longitude()
                        ))
                        .toList(),
                routeEntity.distance(),
                routeEntity.routeId()
        );
    }

}
