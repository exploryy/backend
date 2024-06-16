package com.hits.open.world.public_interface.multipolygon;

public record PolygonRequestDto(
        CreatePolygonRequestDto createPolygonRequestDto,
        String userId
) {
}
