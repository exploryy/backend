package com.hits.open.world.core.websocket.storage;

import com.google.gson.Gson;
import com.hits.open.world.core.websocket.storage.key.SessionKey;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class WebSocketStorage {
    private static final Map<SessionKey, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(final SessionKey sessionKey, final WebSocketSession session) {
        sessions.put(sessionKey, session);
    }

    public void sendMessage(final SessionKey sessionKey, final String message) {
        WebSocketSession session = sessions.get(sessionKey);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = gson.toJson(message);
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                throw new ExceptionInApplication("Exception while sending a message", ExceptionType.INVALID);
            }
        }
    }

    public void remove(@NotNull final WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
            sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        } catch (IOException e) {
            throw new ExceptionInApplication("Exception while removing a session", ExceptionType.INVALID);
        }
    }

}
