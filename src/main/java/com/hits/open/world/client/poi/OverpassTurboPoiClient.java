package com.hits.open.world.client.poi;

import com.hits.open.world.core.poi.PoiEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
public class OverpassTurboPoiClient implements PoiClient {
    private final WebClient webClient;

    public List<PoiEntity> getPoiByCityName(String cityName) {
        var response = webClient.post()
                .bodyValue("""
                        [out:json];
                        area["name"="%s"]->.searchArea;
                        node[tourism](area.searchArea);
                        out;
                        """.formatted(cityName))
                .retrieve()
                .bodyToMono(OverpassTurboPoiResponse.class)
                .block();

        return response.elements().stream()
                .map(this::fromElement)
                .filter(poi -> poi.name() != null && !poi.name().isBlank())
                .toList();
    }

    private PoiEntity fromElement(OverpassTurboPoiResponse.Element element) {
        return new PoiEntity(
                element.tags().name(),
                element.lat(),
                element.lon()
        );
    }
}
