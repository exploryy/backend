package com.hits.open.world.rest.multipolygon;

import com.hits.open.world.core.multipolygon.MultipolygonService;
import com.hits.open.world.public_interface.multipolygon.AreaDtoResponse;
import com.hits.open.world.public_interface.multipolygon.CreatePolygonRequestDto;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/multipolygon")
public class MultiPolygonController {
    private final MultipolygonService multipolygonService;

    @GetMapping
    public GeoDto getMyPolygons(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return multipolygonService.getAllPolygons(userId);
    }

    @GetMapping("/area")
    public AreaDtoResponse calculateArea(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return multipolygonService.calculateArea(userId);
    }

    @DeleteMapping
    public void deleteMultipolygon(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        multipolygonService.delete(userId);
    }

}
