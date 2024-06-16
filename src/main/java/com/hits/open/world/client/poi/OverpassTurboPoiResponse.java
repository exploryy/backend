package com.hits.open.world.client.poi;

import java.util.List;

public record OverpassTurboPoiResponse(
        List<Element> elements
) {
    public record Element(
            String type,
            long id,
            double lat,
            double lon,
            Tags tags
    ) {
        public record Tags(
                String name
        ) {
        }
    }
}
