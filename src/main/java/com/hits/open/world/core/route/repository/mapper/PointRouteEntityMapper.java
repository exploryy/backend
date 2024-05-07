package com.hits.open.world.core.route.repository.mapper;

import com.example.open_the_world.public_.tables.records.PointRouteRecord;
import com.hits.open.world.core.route.repository.PointRouteEntity;
import org.jooq.RecordMapper;

public class PointRouteEntityMapper implements RecordMapper<PointRouteRecord, PointRouteEntity> {
    @Override
    public PointRouteEntity map(PointRouteRecord pointRouteRecord) {
        return new PointRouteEntity(
                pointRouteRecord.getLongitude(),
                pointRouteRecord.getLatitude(),
                pointRouteRecord.getNextLongitude(),
                pointRouteRecord.getNextLatitude()
        );
    }
}
