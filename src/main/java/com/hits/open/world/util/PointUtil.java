package com.hits.open.world.util;

import com.hits.open.world.public_interface.route.PointDto;

import java.util.List;

public class PointUtil {
    public static double distanceInMeters(List<PointDto> points) {
        return points.stream()
                .filter(point -> point.nextLatitude() != null && point.nextLongitude() != null)
                .mapToDouble(point -> DistanceCalculator.calculateDistanceInMeters(
                        Double.parseDouble(point.latitude()),
                        Double.parseDouble(point.longitude()),
                        Double.parseDouble(point.nextLatitude()),
                        Double.parseDouble(point.nextLongitude())
                ))
                .sum();
    }
}
