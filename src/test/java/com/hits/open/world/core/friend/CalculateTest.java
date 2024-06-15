package com.hits.open.world.core.friend;


import com.hits.open.world.util.DistanceCalculator;
import org.junit.jupiter.api.Test;

public class CalculateTest {

    @Test
    public void distanceBetweenTwoPoints() {
        var firstPointLatitude = 56.470569;
        var firstPointLongitude = 84.937810;

        var secondPointLatitude = 56.470317;
        var secondPointLongitude = 84.937039;

        var distance = DistanceCalculator.calculateDistanceInMeters(firstPointLatitude, firstPointLongitude, secondPointLatitude, secondPointLongitude);

        System.out.println(distance);
    }
}
