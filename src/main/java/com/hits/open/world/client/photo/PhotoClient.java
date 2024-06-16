package com.hits.open.world.client.photo;

import java.util.List;

public interface PhotoClient {
    List<String> getPhotosByCoordinates(double latitude, double longitude);
}
