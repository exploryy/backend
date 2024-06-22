package com.hits.open.world.core.statistic.repository;

import java.util.List;
import java.util.Optional;

public interface StatisticRepository {
    Optional<StatisticEntity> findByClientId(String clientId);

    StatisticEntity save(StatisticEntity statistic);

    void updateStatistic(StatisticEntity statistic);

    List<StatisticEntity> findAllStatisticByExperienceDesc();

    List<StatisticEntity> findAllStatisticByDistanceDesc();
}
