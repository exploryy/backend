package com.hits.open.world.websocket.experience;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.core.websocket.storage.key.enums.WebSocketHandlerType;
import com.hits.open.world.core.websocket.storage.key.SessionKey;
import com.hits.open.world.core.websocket.storage.WebSocketStorage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.security.Principal;


@Slf4j
@Component
@RequiredArgsConstructor
public class ExperienceHandler extends AbstractWebSocketHandler {
    private final WebSocketStorage webSocketStorage;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        var userId = getUserId(session);
        SessionKey sessionKey = new SessionKey(userId, WebSocketHandlerType.EXPERIENCE);
        webSocketStorage.add(sessionKey, session);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        webSocketStorage.remove(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        webSocketStorage.remove(session);
    }

    private String getUserId(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getTokenAttributes().get("sub").toString();
        }
        throw new ExceptionInApplication("Invalid principal", ExceptionType.INVALID);
    }

}
