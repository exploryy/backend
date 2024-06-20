package com.hits.open.world.core.buff.repository.entity;

import lombok.Builder;

@Builder
public record ClientBufEntity(
    String clientId,
    Long buffId
) {
}
