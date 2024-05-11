package com.hits.open.world.websocket.multipolygon;

import com.google.gson.Gson;
import com.hits.open.world.core.multipolygon.MultipolygonService;
import com.hits.open.world.public_interface.multipolygon.CoordinateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;


@Slf4j
@Component
@RequiredArgsConstructor
public class MultipolygonHandler extends AbstractWebSocketHandler {
    private static final Gson objectMapper = new Gson();
    private final MultipolygonService multipolygonService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Successfully connection established on session: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        //TODO: Write correct parsing of token, take userId from token using keycloak
        String token = session.getHandshakeHeaders().get("Authorization").getFirst();
        String parsedMessage = (String) message.getPayload();
        CoordinateDto coordinateDto = objectMapper.fromJson(parsedMessage, CoordinateDto.class);

        var changedPolygon = multipolygonService.save(coordinateDto, token);
        String response = objectMapper.toJson(changedPolygon);
        session.sendMessage(new TextMessage(response));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Exception occurred: {} on session: {}", exception.getMessage(), session.getId());
        session.close(SERVER_ERROR.withReason(exception.getMessage()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
    }

}
