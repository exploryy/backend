package com.hits.open.world.core.websocket.storage;

import com.google.gson.Gson;
import com.hits.open.world.core.websocket.storage.key.SessionKey;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Iterator;
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

    public void sendMessage(final SessionKey sessionKey, final Object message) {
        WebSocketSession session = sessions.get(sessionKey);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = gson.toJson(message);
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                throw new ExceptionInApplication("Exception while sending a message", ExceptionType.INVALID);
            }
        } else {
            throw new ExceptionInApplication("Session not open or not exists", ExceptionType.INVALID);
        }
    }

    public void remove(final SessionKey sessionKey) {
        WebSocketSession session = sessions.remove(sessionKey);

        if (session != null) {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (IOException e) {
                throw new ExceptionInApplication("Exception while removing a message", ExceptionType.INVALID);
            }
        } else {
            throw new ExceptionInApplication("Session not open or not exists", ExceptionType.INVALID);
        }
    }

    public void remove(final WebSocketSession session) {
        if (session != null) {
            try {
                if (session.isOpen()) {
                    session.close();
                }
                sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
            } catch (IOException e) {
                throw new ExceptionInApplication("Exception while removing a session", ExceptionType.INVALID);
            }
        } else {
            throw new ExceptionInApplication("Session is null", ExceptionType.INVALID);
        }
    }

    @Scheduled(fixedDelayString = "${websocket.storage.fixedDelay.in.milliseconds}")
    private void cleanUpClosedSessions() {
        Iterator<Map.Entry<SessionKey, WebSocketSession>> iterator = sessions.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<SessionKey, WebSocketSession> entry = iterator.next();

            WebSocketSession session = entry.getValue();

            if (!session.isOpen()) {
                iterator.remove();
            }
        }
    }

}
