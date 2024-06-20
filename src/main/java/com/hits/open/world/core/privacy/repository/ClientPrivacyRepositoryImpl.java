package com.hits.open.world.core.privacy.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.example.open_the_world.public_.tables.ClientPrivacy.CLIENT_PRIVACY;

@Repository
@RequiredArgsConstructor
public class ClientPrivacyRepositoryImpl implements ClientPrivacyRepository {
    private DSLContext create;

    @Override
    public void setPrivacy(ClientPrivacyEntity clientPrivacyEntity) {
        tryCreateUserPrivacy(clientPrivacyEntity.clientId());
        create.update(CLIENT_PRIVACY)
                .set(CLIENT_PRIVACY.IS_PRIVATE, clientPrivacyEntity.isPrivate())
                .where(CLIENT_PRIVACY.CLIENT_ID.eq(clientPrivacyEntity.clientId()))
                .execute();
    }

    @Override
    public boolean isPublic(String clientId) {
        tryCreateUserPrivacy(clientId);
        return Boolean.TRUE.equals(create.select(CLIENT_PRIVACY.IS_PRIVATE)
                .from(CLIENT_PRIVACY)
                .where(CLIENT_PRIVACY.CLIENT_ID.eq(clientId))
                .fetchOne(CLIENT_PRIVACY.IS_PRIVATE));
    }

    private void tryCreateUserPrivacy(String clientId) {
        var userPrivacy = create.select(CLIENT_PRIVACY.IS_PRIVATE)
                .from(CLIENT_PRIVACY)
                .where(CLIENT_PRIVACY.CLIENT_ID.eq(clientId))
                .fetchOne(CLIENT_PRIVACY.IS_PRIVATE);
        if (userPrivacy == null) {
            initializePrivacy(clientId);
        }
    }

    private void initializePrivacy(String clientId) {
        create.insertInto(CLIENT_PRIVACY)
                .set(CLIENT_PRIVACY.CLIENT_ID, clientId)
                .set(CLIENT_PRIVACY.IS_PRIVATE, false)
                .execute();
    }
}
