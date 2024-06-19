package com.hits.open.world.util;

import com.hits.open.world.public_interface.route.PointDto;

import java.util.List;

public class PointUtil {
    public static double distanceInMeters(List<PointDto> points) {
        return points.stream()
                .limit(points.size() - 1)
                .mapToDouble(point -> DistanceCalculator.calculateDistanceInMeters(
                        Double.parseDouble(point.latitude()),
                        Double.parseDouble(point.longitude()),
                        Double.parseDouble(points.get(points.indexOf(point) + 1).latitude()),
                        Double.parseDouble(points.get(points.indexOf(point) + 1).longitude())
                ))
                .sum();
    }
}
