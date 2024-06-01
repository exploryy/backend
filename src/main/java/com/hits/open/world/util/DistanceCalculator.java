package com.hits.open.world.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class DistanceCalculator {

    private DistanceCalculator() {
        throw new UnsupportedOperationException();
    }

    public static int calculateDistance(final BigDecimal x1, final BigDecimal y1, final BigDecimal x2, final BigDecimal y2) {
        BigDecimal dx = x2.subtract(x1);
        BigDecimal dy = y2.subtract(y1);

        BigDecimal dxSquared = dx.pow(2);
        BigDecimal dySquared = dy.pow(2);

        BigDecimal sum = dxSquared.add(dySquared);

        BigDecimal distance = sum.sqrt(new MathContext(10, RoundingMode.HALF_UP));

        return distance.intValue();
    }
}
