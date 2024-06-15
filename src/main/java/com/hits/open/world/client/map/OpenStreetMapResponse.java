package com.hits.open.world.client.map;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenStreetMapResponse(
        String code,
        List<Routes> routes
) {
    public record Routes(
            List<Leg> legs,
            @JsonProperty("weight_name")
            String weightName,
            double weight,
            double duration,
            double distance
    ) {
        public record Leg(
                List<Step> steps,
                double weight,
                double duration,
                double distance,
                String summary
        ) {
            public record Step(
                    String geometry,
                    Maneuver maneuver,
                    String mode,
                    @JsonProperty("driving_side")
                    String drivingSide,
                    String name,
                    List<Intersection> intersections,
                    double weight,
                    double duration,
                    double distance
            ) {
                public record Maneuver(
                        @JsonProperty("bearing_after")
                        double bearingAfter,
                        @JsonProperty("bearing_before")
                        double bearingBefore,
                        List<Double> location,
                        String type,
                        String modifier
                ) {
                }

                public record Intersection(
                        double in,
                        List<Boolean> entry,
                        List<Double> bearings,
                        List<Double> location
                ) {
                }
            }
        }
    }
}
