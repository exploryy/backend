package com.hits.open.world.rest.controller.coin;

import com.hits.open.world.core.coin.CoinService;
import com.hits.open.world.core.money.MoneyService;
import com.hits.open.world.public_interface.coin.BalanceResponseDto;
import com.hits.open.world.public_interface.coin.CoinRequestDto;
import com.hits.open.world.public_interface.coin.CoinResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coin")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Coin")
public class CoinController {
    private final CoinService coinService;
    private final MoneyService moneyService;

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
        return new BalanceResponseDto(
                moneyService.getUserMoney(userId)
        );
    }
}
