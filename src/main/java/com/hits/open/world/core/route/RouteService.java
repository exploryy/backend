package com.hits.open.world.core.route;

import com.hits.open.world.core.route.repository.RouteRepository;
import com.hits.open.world.public_interface.route.CreateRouteDto;
import com.hits.open.world.public_interface.route.RouteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

    public void createRoute(CreateRouteDto dto) {

    }

    public RouteDto getRoute(Long routeId) {
        return null;
    }
}
