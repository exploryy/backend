package com.hits.open.world.websocket.multipolygon;

import com.google.gson.Gson;
import com.hits.open.world.core.multipolygon.MultipolygonService;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.multipolygon.CreatePolygonRequestDto;
import com.hits.open.world.public_interface.multipolygon.CreatePolygonResponseDto;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.math.BigDecimal;
import java.security.Principal;

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
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        var userId = getUserId(session);
        var coordinateDto = parseCoordinate(message);

        GeoDto geoDto = multipolygonService.save(coordinateDto, userId);
        BigDecimal areaPercent = multipolygonService.calculatePercentArea(userId);
        CreatePolygonResponseDto createPolygonResponseDto = new CreatePolygonResponseDto(geoDto, areaPercent);
        var response = objectMapper.toJson(createPolygonResponseDto);
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

    private String getUserId(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getTokenAttributes().get("sub").toString();
        }
        throw new ExceptionInApplication("Invalid principal", ExceptionType.INVALID);
    }

    private CreatePolygonRequestDto parseCoordinate(WebSocketMessage<?> message) {
        Object payload = message.getPayload();
        if (payload instanceof String parsedPayload) {
            return objectMapper.fromJson(parsedPayload, CreatePolygonRequestDto.class);
        }
        throw new ExceptionInApplication("Invalid payload", ExceptionType.INVALID);
    }

}
