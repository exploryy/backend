package com.hits.open.world.core.multipolygon.factory.polygon;

import com.hits.open.world.core.multipolygon.enums.FigureType;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public interface PolygonService {
    FigureType getType();

    Polygon constructPolygon(Point centralPoint);
}
