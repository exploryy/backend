package com.hits.open.world.core.privacy.repository;

public interface ClientPrivacyRepository {
    void setPrivacy(ClientPrivacyEntity clientPrivacyEntity);
    boolean isPublic(String clientId);
}
