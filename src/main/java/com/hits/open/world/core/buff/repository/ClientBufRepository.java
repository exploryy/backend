package com.hits.open.world.core.buff.repository;

import com.hits.open.world.core.buff.repository.entity.ClientBufEntity;

import java.util.List;
import java.util.Optional;

public interface ClientBufRepository {
    List<ClientBufEntity> findBuffsByUserId(String userId);
    Optional<ClientBufEntity> findByBuffIdAndUserId(Long buffId, String userId);
    void save(ClientBufEntity clientBufEntity);
}
