package com.hits.open.world.core.websocket.client;

import com.hits.open.world.core.websocket.storage.WebSocketStorage;
import com.hits.open.world.core.websocket.storage.key.SessionKey;
import com.hits.open.world.core.websocket.storage.key.enums.WebSocketHandlerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketClient {
    private final WebSocketStorage webSocketStorage;

    public void sendUserExperience(String userId, String message) {
        var sessionKey = new SessionKey(userId, WebSocketHandlerType.EXPERIENCE);
        webSocketStorage.sendMessage(sessionKey, message);
    }

    public void sendUserLevel(String userId, String message) {
        var sessionKey = new SessionKey(userId, WebSocketHandlerType.LEVEL);
        webSocketStorage.sendMessage(sessionKey, message);
    }

    public void sendFriendsPosition(String userId, String message) {
        var sessionKey = new SessionKey(userId, WebSocketHandlerType.FRIEND_POSITION);
        webSocketStorage.sendMessage(sessionKey, message);
    }

    public void sendEvent(String userId, String eventDto) {
        var sessionKey = new SessionKey(userId, WebSocketHandlerType.EVENT);
        webSocketStorage.sendMessage(sessionKey, eventDto);
    }
}
