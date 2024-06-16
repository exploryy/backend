package com.hits.open.world.core.friend;

import com.hits.open.world.core.poi.PoiService;
import com.hits.open.world.core.quest.QuestGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest
@SpringJUnitConfig(classes = {QuestGenerationConfig.class})
@ActiveProfiles("local")
class QuestGenerationTest {
    @Autowired
    private QuestGenerationService questGenerationService;
    @Autowired
    private PoiService poiService;

    @Test
    void generateDistanceQuest() {
        var randomPoi = poiService.getRandomPoiInCity("Томск");
        var result = questGenerationService.generateRandomDistanceQuest(randomPoi);
        System.out.println(result);
    }

    @Test
    void generatePointToPointQuest() {
        var randomPoi = poiService.getRandomPoiInCity("Томск");
        var randomPoi2 = poiService.getRandomPoiInCity("Томск");
        var result = questGenerationService.generateRandomPointToPointQuest(randomPoi, randomPoi2);
        System.out.println(result);
    }
}
