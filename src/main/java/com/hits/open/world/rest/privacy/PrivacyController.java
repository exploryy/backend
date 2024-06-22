package com.hits.open.world.rest.privacy;

import com.hits.open.world.core.privacy.ClientPrivacyService;
import com.hits.open.world.public_interface.privacy.PrivacyDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/privacy")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Privacy")
public class PrivacyController {
    private final ClientPrivacyService clientPrivacyService;

    @PatchMapping
    public void setPrivacy(@RequestParam("isPublic") boolean isPublic,
                           JwtAuthenticationToken token
    ) {
        var userId = token.getTokenAttributes().get("sub").toString();
        clientPrivacyService.setPrivacy(userId, isPublic);
    }

    @GetMapping
    public PrivacyDto getPrivacy(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return clientPrivacyService.getPrivacy(userId);
    }
}
