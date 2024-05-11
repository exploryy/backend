package com.hits.open.world.core.multipolygon.repository;

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
                INSERT INTO multipolygon(user_id, geom)
                VALUES (:userId, ST_GeomFromText(:polygon, 4326))
                ON CONFLICT (user_id) DO UPDATE
                SET geom = ST_Union(multipolygon.geom, excluded.geom)
                RETURNING ST_AsGeoJSON(geom);
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("polygon", polygon.toString());

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    @Override
    public void delete(String userId) {
        var sql = """
                DELETE FROM multipolygon
                WHERE user_id = :userId;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public BigDecimal calculateArea(String userId) {
        try {
            var sql = """
                    SELECT ST_Area(ST_Transform(geom, 26986)) AS sqm
                    FROM multipolygon
                    WHERE user_id = :userId;
                    """;

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("userId", userId);

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
                    WHERE user_id = :userId;
                    """;

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("userId", userId);
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}

