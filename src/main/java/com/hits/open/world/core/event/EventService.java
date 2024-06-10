package com.hits.open.world.core.event;

import com.google.gson.Gson;
import com.hits.open.world.core.websocket.client.WebSocketClient;
import com.hits.open.world.public_interface.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private static final Gson objectMapper = new Gson();
    private final WebSocketClient webSocketClient;

    public void sendEvent(String userId, EventDto eventDto) {
        var event = objectMapper.toJson(eventDto);
        webSocketClient.sendEvent(userId, event);
    }
}
