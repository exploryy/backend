package com.hits.open.world.rest.controller.coin;

import com.hits.open.world.core.coin.CoinService;
import com.hits.open.world.core.money.MoneyService;
import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.public_interface.coin.BalanceResponseDto;
import com.hits.open.world.public_interface.coin.CoinResponseDto;
import com.hits.open.world.util.LevelUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coin")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Coin")
public class CoinController {
    private final CoinService coinService;
    private final MoneyService moneyService;
    private final StatisticService statisticService;

    @GetMapping(path = "/list")
    public List<CoinResponseDto> findAll(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return coinService.findAll(userId);
    }

    @PatchMapping(path = "/consume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void consume(@RequestParam("coin_id") Long coinId,
                        JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        coinService.consumeCoin(coinId, userId);
    }

    @GetMapping(path = "/balance")
    public BalanceResponseDto getBalance(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var experience = statisticService.getUserStatistics(userId).experience();
        var level = LevelUtil.calculateLevel(experience);

        return new BalanceResponseDto(
                moneyService.getUserMoney(userId),
                experience,
                level,
                LevelUtil.calculateTotalExperienceInLevel(level)
        );
    }
}
