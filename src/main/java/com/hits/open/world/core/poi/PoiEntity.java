package com.hits.open.world.core.poi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PoiEntity(
        String name,
        double review,
        @JsonProperty("count_review")
        int countReview,
        double latitude,
        double longitude
) {
}
