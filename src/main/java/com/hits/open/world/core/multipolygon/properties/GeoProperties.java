package com.hits.open.world.core.multipolygon.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "geo")
public class GeoProperties {
    private double earthRadius;
    private double maxDistance;
}
