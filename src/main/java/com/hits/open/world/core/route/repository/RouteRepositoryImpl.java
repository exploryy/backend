package com.hits.open.world.core.route.repository;

import com.hits.open.world.core.route.repository.mapper.PointRouteEntityMapper;
import com.hits.open.world.core.route.repository.mapper.RouteEntityMapper;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.Tables.POINT_ROUTE;
import static com.example.open_the_world.public_.Tables.ROUTE;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;

@Repository
@RequiredArgsConstructor
public class RouteRepositoryImpl implements RouteRepository {
    private static final PointRouteEntityMapper POINT_ROUTE_ENTITY_MAPPER = new PointRouteEntityMapper();
    private static final RouteEntityMapper ROUTE_ENTITY_MAPPER = new RouteEntityMapper();

    private final DSLContext create;

    @Override
    public void savePoints(List<PointRouteEntity> pointRouteEntity) {
        create.batchInsert(
                pointRouteEntity.stream()
                        .map(point -> create.newRecord(POINT_ROUTE)
                                .values(point.latitude(), point.longitude(), point.nextLatitude(), point.nextLongitude())
                        ).toList()
        ).execute();

    }

    @Override
    public RouteEntity saveRoute(RouteEntity routeEntity) {
        return create.insertInto(ROUTE)
                .set(ROUTE.DISTANCE, routeEntity.distance())
                .set(ROUTE.POINT_LATITUDE, routeEntity.pointLatitude())
                .set(ROUTE.POINT_LONGITUDE, routeEntity.pointLongitude())
                .returning(ROUTE.ROUTE_ID, ROUTE.DISTANCE, ROUTE.POINT_LATITUDE, ROUTE.POINT_LONGITUDE)
                .fetchOne(ROUTE_ENTITY_MAPPER);
    }

    @Override
    public Optional<RouteEntity> getRoute(Long routeId) {
        return create.selectFrom(ROUTE)
                .where(ROUTE.ROUTE_ID.eq(routeId))
                .fetchOptional(ROUTE_ENTITY_MAPPER);
    }

    @Override
    public List<PointRouteEntity> getPointsInRoute(Long routeId) {
        var route = getRoute(routeId)
                .orElseThrow(() -> new ExceptionInApplication("Route not found", ExceptionType.NOT_FOUND));

        var cte = name("cte")
                .fields("latitude", "longitude", "next_latitude", "next_longitude")
                .as(
                        create.select(POINT_ROUTE.LATITUDE, POINT_ROUTE.LONGITUDE, POINT_ROUTE.NEXT_LATITUDE, POINT_ROUTE.NEXT_LONGITUDE)
                                .from(POINT_ROUTE)
                                .where(POINT_ROUTE.LATITUDE.eq(route.pointLatitude())
                                        .and(POINT_ROUTE.LONGITUDE.eq(route.pointLongitude()))
                                )
                                .unionAll(
                                        create.select(POINT_ROUTE.LATITUDE, POINT_ROUTE.LONGITUDE, POINT_ROUTE.NEXT_LATITUDE, POINT_ROUTE.NEXT_LONGITUDE)
                                                .from(POINT_ROUTE)
                                                .join(name("cte"))
                                                .on(field("cte.next_latitude").eq(POINT_ROUTE.LATITUDE)
                                                        .and(field("cte.next_longitude").eq(POINT_ROUTE.LONGITUDE)
                                                        )
                                                )
                                )
                );

        return create.with(cte)
                .selectFrom(cte)
                .fetch(record -> new PointRouteEntity(
                        record.get(POINT_ROUTE.LONGITUDE, String.class),
                        record.get(POINT_ROUTE.LATITUDE, String.class),
                        record.get(POINT_ROUTE.NEXT_LONGITUDE, String.class),
                        record.get(POINT_ROUTE.NEXT_LATITUDE, String.class)
                ));
    }
}
