package com.hits.open.world.rest.controller.buff;

import com.example.open_the_world.public_.tables.Buff;
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
@RequestMapping("/buff")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Buff")
public class BuffController {
    private final BuffService buffService;

    @GetMapping("/my")
    public List<BuffDto> getMyBuffs(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return buffService.getMyBuffs(userId);
    }

    @GetMapping("/all")
    public List<BuffDto> getAll(@RequestParam(required = false) Integer level) {
        return buffService.getAll(level);
    }

    @PostMapping("/apply")
    public BuffDto applyBuff(@RequestParam Long buffId,
                                   JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return buffService.applyBuff(buffId, userId);
    }
}
