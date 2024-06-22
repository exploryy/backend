package com.hits.open.world.client.map;

import com.hits.open.world.core.quest.repository.entity.generated.GeneratedPoint;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.public_interface.client.map.WayFromPointToPointDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class OpenStreetMapClient implements MapClient {
    private final WebClient webClient;
    private final String carUri;
    private final String footUri;
    private final String bicycleUri;

    public WayFromPointToPointDto getRoadBetweenTwoPoints(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude, TransportType transportType) {
        var uri = "%s/%s,%s;%s,%s".formatted(getUri(transportType), fromLongitude, fromLatitude, toLongitude, toLatitude);
        var response = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(uri)
                                .queryParam("overview", false)
                                .queryParam("steps", true)
                                .build()
                )
                .retrieve()
                .bodyToMono(OpenStreetMapResponse.class)
                .block();

        var way = response.routes().get(0);
        return new WayFromPointToPointDto(
                way.legs().get(0).steps().stream()
                        .map(this::fromSteps)
                        .toList(),
                way.distance()
        );
    }

    private String getUri(TransportType transportType) {
        return switch (transportType) {
            case CAR -> carUri;
            case WALK -> footUri;
            case BICYCLE -> bicycleUri;
        };
    }

    private GeneratedPoint fromSteps(OpenStreetMapResponse.Routes.Leg.Step step) {
        return new GeneratedPoint(
                String.valueOf(step.maneuver().location().get(1)),
                String.valueOf(step.maneuver().location().get(0))
        );
    }
}
