package com.hits.open.world.core.coin.repository;

import java.util.List;
import java.util.Optional;

public interface CoinRepository {
    CoinEntity save(CoinEntity coinEntity);

    Optional<CoinEntity> findById(long id);

    void deleteById(long id);

    List<CoinEntity> findAll(String userId);

    void deleteAll(String userId);
}
