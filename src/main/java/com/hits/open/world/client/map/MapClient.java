package com.hits.open.world.client.map;

import com.hits.open.world.core.quest.repository.entity.generated.GeneratedPoint;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;

import java.util.List;

public interface MapClient {
    List<GeneratedPoint> getRoadBetweenTwoPoints(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude, TransportType transportType);
}
