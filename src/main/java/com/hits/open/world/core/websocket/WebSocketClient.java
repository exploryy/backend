package com.hits.open.world.core.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketClient {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendUserExperience(String userId, String message) {
        messagingTemplate.convertAndSend("/topic/experience/" + userId, message);
    }

    public void sendUserLevel(String userId, String message) {
        messagingTemplate.convertAndSend("/topic/level/" + userId, message);
    }

    public void sendFriendsPosition(String userId, String message) {
        messagingTemplate.convertAndSend("/topic/friends/position/" + userId, message);
    }

}
