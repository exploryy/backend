package com.hits.open.world.core.friend;

import com.hits.open.world.config.ClientBeans;
import com.hits.open.world.core.poi.PoiService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({
        PoiService.class,
        ClientBeans.class,
})
public class PoiImportConfig {
}
