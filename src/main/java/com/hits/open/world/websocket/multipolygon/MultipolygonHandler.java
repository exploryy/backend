package com.hits.open.world.websocket.multipolygon;

import com.google.gson.Gson;
import com.hits.open.world.core.location.UserLocationService;
import com.hits.open.world.core.multipolygon.MultipolygonService;
import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.multipolygon.CreatePolygonRequestDto;
import com.hits.open.world.public_interface.multipolygon.CreatePolygonResponseDto;
import com.hits.open.world.public_interface.multipolygon.PolygonRequestDto;
import com.hits.open.world.public_interface.statistic.UpdateStatisticDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;


@Slf4j
@Component
@RequiredArgsConstructor
public class MultipolygonHandler extends AbstractWebSocketHandler {
    private static final Gson mapper = new Gson();
    private final MultipolygonService multipolygonService;
    private final UserLocationService userLocationService;
    private final StatisticService statisticService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Successfully connection established on session: {}", session.getId());
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

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws IOException {
        var userId = getUserId(session);
        var createPolygonRequestDto = parseCoordinate(message);

        var polygonRequestDto = new PolygonRequestDto(createPolygonRequestDto, userId);
        processLocationInfo(session, polygonRequestDto);
    }

    private String getUserId(WebSocketSession session) {
        var principal = session.getPrincipal();

        if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getTokenAttributes().get("sub").toString();
        }

        throw new ExceptionInApplication("Invalid principal", ExceptionType.INVALID);
    }

    private CreatePolygonRequestDto parseCoordinate(WebSocketMessage<?> message) {
        var payload = message.getPayload();

        if (payload instanceof String parsedPayload) {
            return mapper.fromJson(parsedPayload, CreatePolygonRequestDto.class);
        }

        throw new ExceptionInApplication("Invalid payload", ExceptionType.INVALID);
    }

    private void processLocationInfo(WebSocketSession session, PolygonRequestDto requestDto) throws IOException {
        userLocationService.updateUserLocation(requestDto);

        updateStatistics(requestDto, session);
        multipolygonService.save(requestDto);

        var response = buildPolygonResponse(requestDto);
        session.sendMessage(new TextMessage(response));
    }

    private void updateStatistics(PolygonRequestDto requestDto, WebSocketSession session) {
        var isNewTerritory = multipolygonService.isNewTerritory(requestDto);
        var updateStatisticDto = buildUpdateStatisticDto(requestDto, session, isNewTerritory);

        statisticService.tryUpdateStatistic(updateStatisticDto);
    }

    private UpdateStatisticDto buildUpdateStatisticDto(PolygonRequestDto requestDto, WebSocketSession session, boolean isNewTerritory) {
        return UpdateStatisticDto.builder()
                .lastUpdate(OffsetDateTime.now())
                .longitude(requestDto.createPolygonRequestDto().longitude())
                .latitude(requestDto.createPolygonRequestDto().latitude())
                .isNewTerritory(isNewTerritory)
                .userId(requestDto.userId())
                .webSessionId(session.getId())
                .build();
    }

    private String buildPolygonResponse(PolygonRequestDto requestDto) {
        var areaPercent = multipolygonService.calculatePercentArea(requestDto);
        var geoDto = multipolygonService.getAllPolygons(requestDto.userId());

        var createPolygonResponseDto = new CreatePolygonResponseDto(geoDto, areaPercent);
        return mapper.toJson(createPolygonResponseDto);
    }

}
