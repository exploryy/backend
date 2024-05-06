package com.hits.open.world.rest.quest;

import com.hits.open.world.core.quest.QuestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quest")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Quest")
public class QuestController {
    private final QuestService questService;


}
