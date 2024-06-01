package com.hits.open.world.rest.statistic;

import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.public_interface.statistic.TotalStatisticDto;
import com.hits.open.world.public_interface.statistic.UserStatisticDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistic")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Statistic")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TotalStatisticDto getAllStatistics(@RequestParam(value = "count", defaultValue = "10") Integer count,
                                              JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return statisticService.getTotal(userId, count);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserStatisticDto getAllUserStatistics(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return statisticService.getUserStatistics(userId);
    }


}
