package com.hits.open.world.core.privacy.repository;

public record ClientPrivacyEntity(
        String clientId,
        boolean isPrivate
) {
}
