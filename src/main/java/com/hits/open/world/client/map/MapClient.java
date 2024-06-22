package com.hits.open.world.client.map;

import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.public_interface.client.map.WayFromPointToPointDto;

public interface MapClient {
    WayFromPointToPointDto getRoadBetweenTwoPoints(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude, TransportType transportType);
}
