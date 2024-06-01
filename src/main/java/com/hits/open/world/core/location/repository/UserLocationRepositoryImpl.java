package com.hits.open.world.core.location.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.open_the_world.public_.tables.UserLocation.USER_LOCATION;

@Repository
@RequiredArgsConstructor
public class UserLocationRepositoryImpl implements UserLocationRepository {
    private static final UserLocationEntityMapper mapper = new UserLocationEntityMapper();
    private final DSLContext create;

    @Override
    public void save(UserLocationEntity entity) {
        create.insertInto(USER_LOCATION)
                .set(USER_LOCATION.CLIENT_ID, entity.clientId())
                .set(USER_LOCATION.LAST_VISITATION, entity.lastVisitation())
                .set(USER_LOCATION.LONGITUDE, entity.longitude())
                .set(USER_LOCATION.LATITUDE, entity.latitude())
                .onConflict(USER_LOCATION.CLIENT_ID)
                .doUpdate()
                .set(USER_LOCATION.LAST_VISITATION, entity.lastVisitation())
                .set(USER_LOCATION.LONGITUDE, entity.longitude())
                .set(USER_LOCATION.LATITUDE, entity.latitude())
                .execute();
    }

    @Override
    public Optional<UserLocationEntity> findById(String userId) {
        return create.selectFrom(USER_LOCATION)
                .where(USER_LOCATION.CLIENT_ID.eq(userId))
                .fetchOptional(mapper);
    }
}