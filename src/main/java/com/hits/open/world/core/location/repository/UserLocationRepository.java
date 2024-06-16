package com.hits.open.world.core.location.repository;

import java.util.Optional;

public interface UserLocationRepository {
    void initLocation(UserLocationEntity entity);

    Optional<UserLocationEntity> findById(String id);

    void update(UserLocationEntity entity);
}
