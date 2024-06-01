package com.hits.open.world.core.coin.repository;

import com.example.open_the_world.public_.tables.records.CoinsRecord;
import org.jooq.RecordMapper;

public class CoinEntityMapper implements RecordMapper<CoinsRecord, CoinEntity> {

    @Override
    public CoinEntity map(CoinsRecord coinsRecord) {
        return new CoinEntity(
                coinsRecord.getCoinId(),
                coinsRecord.getLatitude(),
                coinsRecord.getLongitude(),
                coinsRecord.getValue(),
                coinsRecord.getTaken(),
                coinsRecord.getClientId()
        );
    }
}
