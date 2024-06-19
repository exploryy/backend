package com.hits.open.world.rest.controller.battle_pass;

import com.hits.open.world.core.battle_pass.BattlePassService;
import com.hits.open.world.public_interface.battle_pass.BattlePassDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/battle_pass")
@SecurityRequirement(name = "oauth2")
@Tag(name = "BattlePass")
public class BattlePassController {
    private final BattlePassService battlePassService;

    @GetMapping("/current")
    public BattlePassDto getCurrentBattlePass(JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        return battlePassService.getCurrentBattlePass(userId);
    }

    @GetMapping("/all")
    public List<BattlePassDto> getAllBattlePasses(JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        return battlePassService.getAllBattlePasses(userId);
    }
}
