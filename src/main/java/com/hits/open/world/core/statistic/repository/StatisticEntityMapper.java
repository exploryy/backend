package com.hits.open.world.core.statistic.repository;

import com.example.open_the_world.public_.tables.records.ClientStatisticRecord;
import org.jooq.RecordMapper;

public class StatisticEntityMapper implements RecordMapper<ClientStatisticRecord, StatisticEntity> {

    @Override
    public StatisticEntity map(ClientStatisticRecord clientStatisticRecord) {
        return new StatisticEntity(
                clientStatisticRecord.getClientId(),
                clientStatisticRecord.getExperience(),
                clientStatisticRecord.getDistance(),
                clientStatisticRecord.getWebSessionId(),
                clientStatisticRecord.getPreviousLatitude(),
                clientStatisticRecord.getPreviousLongitude(),
                clientStatisticRecord.getLastUpdate()
        );
    }
}
