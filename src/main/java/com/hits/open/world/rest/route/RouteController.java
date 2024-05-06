package com.hits.open.world.rest.route;

import com.hits.open.world.core.route.RouteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/route")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Route")
public class RouteController {
    private final RouteService routeService;


}
