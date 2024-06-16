package com.hits.open.world.util;

public final class DistanceCalculator {
    private static final double EQUATORIAL_EARTH_RADIUS = 6378.1370D;
    private static final double D2R = (Math.PI / 180D);

    private DistanceCalculator() {
        throw new UnsupportedOperationException();
    }

    public static int calculateDistanceInMeters(double lat1, double long1, double lat2, double long2) {
        return (int) (calculateHaversineInKM(lat1, long1, lat2, long2) * 1000D);
    }

    private static double calculateHaversineInKM(double lat1, double long1, double lat2, double long2) {
        double dlong = (long2 - long1) * D2R;
        double dlat = (lat2 - lat1) * D2R;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R)
                * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        return EQUATORIAL_EARTH_RADIUS * c;
    }
}
