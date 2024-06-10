package com.hits.open.world.core.websocket.storage.key;

import com.hits.open.world.core.websocket.storage.key.enums.WebSocketHandlerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SessionKey {
    private String userId;
    private WebSocketHandlerType webSocketHandlerType;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SessionKey s) {
            return userId.equals(s.userId) && webSocketHandlerType.equals(s.webSocketHandlerType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (userId + webSocketHandlerType).hashCode();
    }
}
