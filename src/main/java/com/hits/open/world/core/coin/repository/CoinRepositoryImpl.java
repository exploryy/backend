package com.hits.open.world.core.coin.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.tables.Coins.COINS;

@Repository
@RequiredArgsConstructor
public class CoinRepositoryImpl implements CoinRepository {
    private static final CoinEntityMapper mapper = new CoinEntityMapper();
    private final DSLContext create;

    @Override
    public CoinEntity save(CoinEntity coinEntity) {
        return create.insertInto(COINS)
                .set(COINS.CLIENT_ID, coinEntity.clientId())
                .set(COINS.LATITUDE, coinEntity.latitude())
                .set(COINS.VALUE, coinEntity.value())
                .set(COINS.LONGITUDE, coinEntity.longitude())
                .returning(COINS.COIN_ID, COINS.LATITUDE, COINS.LONGITUDE, COINS.VALUE, COINS.TAKEN, COINS.CLIENT_ID)
                .fetchOne(mapper);
    }

    @Override
    public Optional<CoinEntity> findById(long id) {
        return create.selectFrom(COINS)
                .where(COINS.COIN_ID.eq(id))
                .fetchOptional(mapper);
    }

    @Override
    public void deleteById(long id) {
        create.deleteFrom(COINS)
                .where(COINS.COIN_ID.eq(id))
                .execute();
    }

    @Override
    public List<CoinEntity> findAll(String clientId) {
        return create.selectFrom(COINS)
                .where(COINS.CLIENT_ID.eq(clientId))
                .fetch(mapper);
    }

    @Override
    public void deleteAll(String clientId) {
        create.deleteFrom(COINS)
                .where(COINS.CLIENT_ID.eq(clientId))
                .execute();
    }
}
