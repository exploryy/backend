package com.hits.open.world.core.multipolygon.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.multipolygon.geo.MultipolygonGeometry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class PolygonClient {
    private static final Gson gson = new Gson();
    private final WebClient nominatimWebClient;
    private final WebClient polygonsWebClient;

    public PolygonClient(@Qualifier("nominatimWebClient") WebClient nominatimWebClient,
                         @Qualifier("polygonsWebClient") WebClient polygonsWebClient) {
        this.nominatimWebClient = nominatimWebClient;
        this.polygonsWebClient = polygonsWebClient;
    }

    public MultipolygonGeometry getPolygonData(int osmId) {
        return polygonsWebClient.get()
                .uri("/get_geojson.py?id={id}&params=0", osmId)
                .retrieve()
                .bodyToMono(MultipolygonGeometry.class)
                .block();
    }

    /**
     * For more information see <a href="https://nominatim.openstreetmap.org">openstreetmap</a>
     */
    public int getNominatimData(String placeName) {
        return nominatimWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("q", placeName)
                        .queryParam("format", "json")
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> getOsmId(response))
                .onErrorResume(ex -> Mono.error(new ExceptionInApplication("Exception while call API", ExceptionType.INVALID)))
                .block();
    }

    private int getOsmId(String jsonResponse) {
        try {
            JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);

            if (jsonArray != null && !jsonArray.isEmpty()) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                return jsonObject.get("osm_id").getAsInt();
            } else {
                throw new ExceptionInApplication("Invalid osm_id format", ExceptionType.INVALID);
            }

        } catch (Exception ex) {
            throw new ExceptionInApplication("Invalid while parsing osm_id", ExceptionType.INVALID);
        }
    }
}
