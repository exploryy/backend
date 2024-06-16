package com.hits.open.world.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "nominatimWebClient")
    public WebClient nominatimWebClient() {
        return WebClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .build();
    }

    @Bean(name = "polygonsWebClient")
    public WebClient polygonsWebClient() {
        return WebClient.builder()
                .baseUrl("https://polygons.openstreetmap.fr")
                .build();
    }
}
