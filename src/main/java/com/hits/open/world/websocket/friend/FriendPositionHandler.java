package com.hits.open.world.websocket.friend;

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

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendPositionHandler extends AbstractWebSocketHandler {
    private final WebSocketStorage webSocketStorage;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        var userId = getUserId(session);
        SessionKey sessionKey = new SessionKey(userId, WebSocketHandlerType.FRIEND_POSITION);
        webSocketStorage.add(sessionKey, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Exception occurred: {} on session: {}", exception.getMessage(), session.getId());
        session.close(SERVER_ERROR.withReason(exception.getMessage()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
    }

    private String getUserId(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getTokenAttributes().get("sub").toString();
        }
        throw new ExceptionInApplication("Invalid principal", ExceptionType.INVALID);
    }
}
