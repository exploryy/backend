package com.hits.open.world.core.multipolygon.repository;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.math.BigDecimal;


public interface MultipolygonRepository {
    String insert(String userId, Polygon polygon);

    void delete(String userId);

    BigDecimal calculateArea(String userId);

    String getAllCoordinates(String userId);

    String getPolygonByPoint(Point point, String userId);

    boolean isPointInPolygon(Point point, String userId);

    BigDecimal calculatePercentArea(String firstMultipolygonId, String secondMultipolygonId);
}

