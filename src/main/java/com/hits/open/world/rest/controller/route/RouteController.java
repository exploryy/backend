package com.hits.open.world.rest.controller.route;

import com.hits.open.world.core.route.RouteService;
import com.hits.open.world.public_interface.route.CreateRouteDto;
import com.hits.open.world.public_interface.route.PointDto;
import com.hits.open.world.public_interface.route.RouteDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/route")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Route")
public class RouteController {
    private final RouteService routeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createRoute(@RequestParam("points") List<PointDto> points) {
        var createDto = new CreateRouteDto(
                points
        );
        routeService.createRoute(createDto);
    }

    @GetMapping(path = "/{route_id}")
    public RouteDto getRoute(@PathVariable("route_id") Long routeId) {
        return routeService.getRoute(routeId);
    }
}
