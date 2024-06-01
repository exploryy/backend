package com.hits.open.world.core.statistic.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.Tables.CLIENT_STATISTIC;

@Repository
@RequiredArgsConstructor
public class StatisticRepositoryImpl implements StatisticRepository {
    private static final StatisticEntityMapper mapper = new StatisticEntityMapper();
    private final DSLContext create;


    @Override
    public Optional<StatisticEntity> findByClientId(String clientId) {
        return create.selectFrom(CLIENT_STATISTIC)
                .where(CLIENT_STATISTIC.CLIENT_ID.eq(clientId))
                .fetchOptional(mapper);
    }

    @Override
    public StatisticEntity save(StatisticEntity statistic) {
        return create.insertInto(CLIENT_STATISTIC)
                .set(CLIENT_STATISTIC.CLIENT_ID, statistic.clientId())
                .set(CLIENT_STATISTIC.DISTANCE, statistic.distance())
                .set(CLIENT_STATISTIC.EXPERIENCE, statistic.experience())
                .set(CLIENT_STATISTIC.WEB_SESSION_ID, statistic.webSessionId())
                .set(CLIENT_STATISTIC.PREVIOUS_LATITUDE, statistic.previousLatitude())
                .set(CLIENT_STATISTIC.PREVIOUS_LONGITUDE, statistic.previousLongitude())
                .returning()
                .fetchOne(mapper);
    }

    @Override
    public void updateStatistic(StatisticEntity statistic) {
        create.update(CLIENT_STATISTIC)
                .set(CLIENT_STATISTIC.DISTANCE, statistic.distance())
                .set(CLIENT_STATISTIC.EXPERIENCE, statistic.experience())
                .set(CLIENT_STATISTIC.WEB_SESSION_ID, statistic.webSessionId())
                .set(CLIENT_STATISTIC.PREVIOUS_LONGITUDE, statistic.previousLongitude())
                .set(CLIENT_STATISTIC.PREVIOUS_LATITUDE, statistic.previousLatitude())
                .where(CLIENT_STATISTIC.CLIENT_ID.eq(statistic.clientId()))
                .execute();
    }

    @Override
    public List<StatisticEntity> findAllStatistic() {
        return create.selectFrom(CLIENT_STATISTIC)
                .orderBy(CLIENT_STATISTIC.EXPERIENCE.desc(), CLIENT_STATISTIC.DISTANCE.desc())
                .fetch(mapper);
    }
}
