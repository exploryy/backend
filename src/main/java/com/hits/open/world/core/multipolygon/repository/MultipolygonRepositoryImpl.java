package com.hits.open.world.core.multipolygon.repository;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;


@Repository
@RequiredArgsConstructor
public class MultipolygonRepositoryImpl implements MultipolygonRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public String insert(String userId, Polygon polygon) {
        var sql = """
                INSERT INTO multipolygon(client_id, geom)
                VALUES (:clientId, ST_GeomFromText(:polygon, 4326))
                ON CONFLICT (client_id) DO UPDATE
                SET geom = ST_Union(multipolygon.geom, excluded.geom)
                RETURNING ST_AsGeoJSON(geom);
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", userId);
        params.addValue("polygon", polygon.toString());

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    @Override
    public void delete(String userId) {
        var sql = """
                DELETE FROM multipolygon
                WHERE client_id = :clientId;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", userId);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public BigDecimal calculateArea(String userId) {
        try {
            var sql = """
                    WITH polygons AS (
                        SELECT geom FROM multipolygon
                        WHERE client_id = :clientId
                    )
                                                
                    SELECT SUM(ST_Area(ST_Transform(geom, 26986))) AS sqm
                    FROM polygons;                    
                    """;

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("clientId", userId);

            return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
        } catch (EmptyResultDataAccessException exception) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getAllCoordinates(String userId) {
        try {
            var sql = """
                    SELECT ST_AsGeoJSON(geom) AS geojson
                    FROM public.multipolygon
                    WHERE client_id = :clientId;
                    """;

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("clientId", userId);
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public String getPolygonByPoint(Point point, String userId) {
        try {
            var sql = """
                    WITH polygon_temporary AS (
                        SELECT (ST_Dump(geom)).geom AS part_geom
                        FROM multipolygon
                        WHERE client_id = :clientId
                    )
                                        
                    SELECT ST_AsGeoJSON(part_geom)::json AS geojson
                    FROM polygon_temporary
                    WHERE ST_Contains(part_geom, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326));
                    """;

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("clientId", userId);
            params.addValue("lon", point.getX());
            params.addValue("lat", point.getY());
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public boolean isPointInPolygon(Point point, String userId) {
        var sql = """
                WITH polygon_temporary AS (
                    SELECT (ST_Dump(geom)).geom AS part_geom
                    FROM multipolygon
                    WHERE client_id = :clientId
                )
                                        
                SELECT EXISTS (
                    SELECT 1
                    FROM polygon_temporary
                    WHERE ST_Contains(part_geom, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326))
                );
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", userId);
        params.addValue("lon", point.getX());
        params.addValue("lat", point.getY());
        return jdbcTemplate.queryForObject(sql, params, Boolean.class);
    }

    @Override
    public BigDecimal calculatePercentArea(String firstMultipolygonId, String secondMultipolygonId) {
        var sql = """
                WITH
                    intersection AS (
                        SELECT ST_Intersection(p1.geom, p2.geom) AS geom
                        FROM multipolygon p1, multipolygon p2
                        WHERE p1.client_id = :firstMultipolygonId AND p2.client_id = :secondMultipolygonId
                    ),
                    intersection_area AS (
                        SELECT ST_Area(geom) AS area
                        FROM intersection
                    ),
                    second_area AS (
                        SELECT ST_Area(geom) AS area
                        FROM multipolygon
                        WHERE client_id = :secondMultipolygonId
                    )
                SELECT (i.area / t.area) * 100 AS percentage
                FROM intersection_area i, second_area t;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("firstMultipolygonId", firstMultipolygonId);
        params.addValue("secondMultipolygonId", secondMultipolygonId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

}

