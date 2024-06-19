package com.hits.open.world.core.route.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.Tables.POINTS;
import static com.example.open_the_world.public_.Tables.ROUTE;
import static com.example.open_the_world.public_.Tables.ROUTE_POINTS;
import static org.jooq.impl.DSL.row;

@Repository
@RequiredArgsConstructor
public class RouteRepositoryImpl implements RouteRepository {
    private final DSLContext create;

    @Override
    public RouteEntity saveRoute(RouteEntity routeEntity) {
        var routeId = create.insertInto(ROUTE)
                .set(ROUTE.DISTANCE, routeEntity.distance())
                .returning(ROUTE.ROUTE_ID)
                .fetchOne()
                .getRouteId();
        var pointsId = savePoints(routeEntity.points());
        create.insertInto(ROUTE_POINTS, ROUTE_POINTS.ROUTE_ID, ROUTE_POINTS.POINT_ID, ROUTE_POINTS.NUMBER)
                .valuesOfRows(pointsId.stream()
                        .map(pointId -> row(routeId, pointId, pointsId.indexOf(pointId)))
                        .toList())
                .execute();
        return new RouteEntity(
                routeId,
                routeEntity.distance(),
                routeEntity.points().stream()
                        .map(point -> new PointEntity(
                                point.latitude(),
                                point.longitude(),
                                pointsId.get(routeEntity.points().indexOf(point))
                        ))
                        .toList()
        );
    }

    @Override
    public Optional<RouteEntity> getRoute(Long routeId) {
        var route = create.selectFrom(ROUTE)
                .where(ROUTE.ROUTE_ID.eq(routeId))
                .fetchOptional();
        if (route.isEmpty()) {
            return Optional.empty();
        }

        var points = create.select(POINTS.LAT, POINTS.LON, POINTS.POINT_ID)
                .from(POINTS)
                .join(ROUTE_POINTS)
                .on(POINTS.POINT_ID.eq(ROUTE_POINTS.POINT_ID))
                .where(ROUTE_POINTS.ROUTE_ID.eq(routeId))
                .orderBy(ROUTE_POINTS.NUMBER)
                .fetch()
                .map(record -> new PointEntity(
                        record.get(POINTS.LAT),
                        record.get(POINTS.LON),
                        record.get(POINTS.POINT_ID)
                ));
        return Optional.of(new RouteEntity(
                routeId,
                route.get().getDistance(),
                points
        ));
    }

    @Override
    public List<Long> savePoints(List<PointEntity> points) {
        return create.insertInto(POINTS, POINTS.LAT, POINTS.LON)
                .valuesOfRows(points.stream()
                        .map(point -> row(point.latitude(), point.longitude()))
                        .toList())
                .returningResult(POINTS.POINT_ID)
                .fetchInto(Long.class);
    }

    @Override
    public Optional<PointEntity> getPoint(Long pointId) {
        return create.selectFrom(POINTS)
                .where(POINTS.POINT_ID.eq(pointId))
                .fetchOptional()
                .map(record -> new PointEntity(
                        record.get(POINTS.LAT),
                        record.get(POINTS.LON),
                        record.get(POINTS.POINT_ID)
                ));
    }
}
