package com.hits.open.world.core.route.repository.mapper;

import com.example.open_the_world.public_.tables.records.RouteRecord;
import com.hits.open.world.core.route.repository.RouteEntity;
import org.jooq.RecordMapper;

public class RouteEntityMapper implements RecordMapper<RouteRecord, RouteEntity> {
    @Override
    public RouteEntity map(RouteRecord routeRecord) {
        return new RouteEntity(
                routeRecord.getRouteId(),
                routeRecord.getDistance(),
                routeRecord.getPointLatitude(),
                routeRecord.getPointLongitude()
        );
    }
}
