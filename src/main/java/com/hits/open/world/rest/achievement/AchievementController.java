package com.hits.open.world.rest.achievement;

import com.hits.open.world.core.achievement.AchievementService;
import com.hits.open.world.public_interface.achievement.AchievementDto;
import com.hits.open.world.public_interface.achievement.CreateAchievementDto;
import com.hits.open.world.public_interface.achievement.UpdateAchievementDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/achievement")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Achievement")
public class AchievementController {
    private final AchievementService achievementService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAchievement(@RequestParam("name") String name,
                                  @RequestParam("description") String description,
                                  @RequestParam("image") MultipartFile image) {
        var createDto = new CreateAchievementDto(
                name,
                description,
                image
        );
        achievementService.createAchievement(createDto);
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAchievement(@RequestParam("achievementId") Long achievementId) {
        achievementService.deleteAchievement(achievementId);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateAchievement(@RequestParam("achievementId") Long achievementId,
                                  @RequestParam("name") Optional<String> name,
                                  @RequestParam("description") Optional<String> description,
                                  @RequestParam("image") Optional<MultipartFile> image) {
        var updateDto = new UpdateAchievementDto(
                achievementId,
                name,
                description,
                image
        );
        achievementService.updateAchievement(updateDto);
    }

    @GetMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AchievementDto> getMyAchievements(JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        return achievementService.getAchievements(userId);
    }
}
