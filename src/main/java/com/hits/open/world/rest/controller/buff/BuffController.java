package com.hits.open.world.rest.controller.buff;

import com.hits.open.world.core.buff.BuffService;
import com.hits.open.world.public_interface.buff.BuffDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/buffs")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Buffs")
public class BuffController {
    private final BuffService buffService;

    @PostMapping("/apply")
    public void applyBuff(@RequestParam("buffId") Long buffId,
                          JwtAuthenticationToken token
    ) {
        var userId = token.getTokenAttributes().get("sub").toString();
        buffService.applyBuff(buffId, userId);
    }

    @GetMapping("/my")
    public List<BuffDto> getMyBuffs(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return buffService.getMyBuffs(userId);
    }

    @GetMapping("/available")
    public List<BuffDto> getAvailableBuffs(@RequestParam(required = false) Integer level) {
        return buffService.getAll(level);
    }

}
