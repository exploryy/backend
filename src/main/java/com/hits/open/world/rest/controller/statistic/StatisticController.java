package com.hits.open.world.rest.controller.statistic;

import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.public_interface.location.LocationStatisticDto;
import com.hits.open.world.public_interface.statistic.TotalStatisticDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistic")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Statistic")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/top/level")
    public TotalStatisticDto getAllStatistics(@RequestParam(value = "count", defaultValue = "10") Integer count,
                                              JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return statisticService.getTopLevel(userId, count);
    }

    @GetMapping("/top/distance")
    public TotalStatisticDto getTopDistance(@RequestParam(value = "count", defaultValue = "10") Integer count,
                                                 JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return statisticService.getTopDistance(userId, count);
    }

    @GetMapping(path = "/my")
    public LocationStatisticDto getAllUserStatistics(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return statisticService.getUserStatistics(userId);
    }

    @GetMapping("/friend/coordinates")
    public List<LocationStatisticDto> getLocationsMyFriend(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return statisticService.getLocationsMyFriend(userId);
    }

    @GetMapping(path = "/friend/{client_id}")
    public LocationStatisticDto getFriendStatistics(@PathVariable("client_id") String clientId,
                                                    JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return statisticService.getFriendStatistic(userId, clientId);
    }
}
