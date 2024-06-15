package com.hits.open.world.core.friend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hits.open.world.client.gpt.YandexGptClient;
import com.hits.open.world.config.ClientBeans;
import com.hits.open.world.core.poi.PoiService;
import com.hits.open.world.core.quest.QuestGenerationService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({
        QuestGenerationService.class,
        PoiService.class,
        ClientBeans.class,
        ObjectMapper.class
})
public class QuestGenerationConfig {
}
