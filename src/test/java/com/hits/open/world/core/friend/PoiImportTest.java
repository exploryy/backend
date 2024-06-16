package com.hits.open.world.core.friend;

import com.hits.open.world.core.poi.PoiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SpringJUnitConfig(classes = {PoiImportConfig.class})
class PoiImportTest {
    @Autowired
    private PoiService poiService;

    @Test
    void importPoiData() {
        poiService.tryLoadPoiData("Томск");
        assertTrue(poiService.getCities().contains("Томск"));
        System.out.println(poiService.getRandomPoiInCity("Томск"));
    }
}
