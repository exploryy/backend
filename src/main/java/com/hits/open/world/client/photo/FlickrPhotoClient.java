package com.hits.open.world.client.photo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
public class FlickrPhotoClient implements PhotoClient {
    private final WebClient webClient;
    private final String apiKey;

    @Override
    public List<String> getPhotosByCoordinates(double latitude, double longitude) {
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "flickr.photos.search")
                        .queryParam("api_key", apiKey)
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("format", "json")
                        .queryParam("nojsoncallback", 1)
                        .queryParam("radius", 0.1)
                        .build())
                .retrieve()
                .bodyToMono(FlickrPhotoResponse.class)
                .block();
        return response.photos().photo().stream()
                .map(FlickrPhotoResponse.Photos.Photo::getUrl)
                .limit(5)
                .toList();
    }
}
