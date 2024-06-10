package com.hits.open.world.core.event;

import com.hits.open.world.core.websocket.WebSocketClient;
import com.hits.open.world.public_interface.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final WebSocketClient webSocketClient;

    public void sendEvent(String userId, EventDto eventDto) {
        webSocketClient.sendEvent(userId, eventDto);
    }
}
