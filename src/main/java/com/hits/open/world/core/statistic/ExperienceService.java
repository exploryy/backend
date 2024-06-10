package com.hits.open.world.core.statistic;

import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.core.statistic.repository.StatisticEntity;
import com.hits.open.world.public_interface.statistic.UpdateStatisticDto;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static com.hits.open.world.util.LevelUtil.calculateLevel;

public final class ExperienceService {
    private static final int NEW_TERRITORY_BUF = 15;
    private static final int MAX_LEVEL_WITH_BUF = 10;
    private static final double LEVEL_COST = 10.0;

    private ExperienceService() {
        throw new UnsupportedOperationException();
    }

    public static int calculateExperienceByTask(int currentExperience, int addedExperience) {
        int level = calculateLevel(currentExperience);
        double levelBuf = 1 + level / LEVEL_COST;
        int addedExperienceBuf = (int) (levelBuf * addedExperience);
        return currentExperience + addedExperienceBuf;
    }

    public static int calculateExperienceByDistance(StatisticEntity statistic, UpdateStatisticDto dto, int distanceInMeters) {
        var time = Math.abs(ChronoUnit.SECONDS.between(OffsetDateTime.now(), statistic.lastUpdate()));
        long speedMetersInSeconds = time == 0 ? 0 : distanceInMeters / Math.abs(ChronoUnit.SECONDS.between(OffsetDateTime.now(), statistic.lastUpdate()));
        TransportType transportType = TransportType.fromSpeedMetersInSeconds(speedMetersInSeconds);
        int level = calculateLevel(statistic.experience());

        int businessDistance = dto.isNewTerritory() ? distanceInMeters : distanceInMeters / NEW_TERRITORY_BUF;
        businessDistance = (level < MAX_LEVEL_WITH_BUF) ? businessDistance : 0;

        double levelBuf = 1 + level / LEVEL_COST;
        double transportBuf = transportType.getExperienceBuff();
        int buffedDistance = (int) (levelBuf * businessDistance * transportBuf);

        return buffedDistance + statistic.experience();
    }

}
