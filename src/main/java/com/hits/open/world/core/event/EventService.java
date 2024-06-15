package com.hits.open.world.core.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hits.open.world.core.websocket.client.WebSocketClient;
import com.hits.open.world.public_interface.event.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private static ObjectMapper objectMapper;
    private final WebSocketClient webSocketClient;

    public void sendEvent(String userId, EventDto eventDto) {
        try {
            var event = objectMapper.writeValueAsString(eventDto);
            webSocketClient.sendEvent(userId, event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Fail json processing", e);
        }
    }
}
