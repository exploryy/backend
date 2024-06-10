package com.hits.open.world.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hits.open.world.websocket.event.EventHandler;
import com.hits.open.world.websocket.exception.WebSocketExceptionInterceptor;
import com.hits.open.world.websocket.experience.ExperienceHandler;
import com.hits.open.world.websocket.friend.FriendPositionHandler;
import com.hits.open.world.websocket.level.LevelHandler;
import com.hits.open.world.websocket.multipolygon.MultipolygonHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;


@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {
    private final MultipolygonHandler polygonWebSocketHandler;
    private final ExperienceHandler experienceHandler;
    private final FriendPositionHandler friendPositionHandler;
    private final LevelHandler levelHandler;
    private final EventHandler eventHandler;

    public WebSocketConfig(@Lazy MultipolygonHandler polygonWebSocketHandler, @Lazy ExperienceHandler experienceHandler,
                           @Lazy LevelHandler levelHandler, @Lazy FriendPositionHandler friendPositionHandler, @Lazy EventHandler eventHandler) {
        this.polygonWebSocketHandler = polygonWebSocketHandler;
        this.experienceHandler = experienceHandler;
        this.levelHandler = levelHandler;
        this.friendPositionHandler = friendPositionHandler;
        this.eventHandler = eventHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user", "/topic");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();

        registry.setErrorHandler(new WebSocketExceptionInterceptor());
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(polygonWebSocketHandler, "/ws/location")
                .setAllowedOrigins("*");
        registry.addHandler(experienceHandler, "ws/experience")
                .setAllowedOriginPatterns("*");
        registry.addHandler(levelHandler, "ws/level")
                .setAllowedOriginPatterns("*");
        registry.addHandler(friendPositionHandler, "ws/friendPosition")
                .setAllowedOriginPatterns("*");
        registry.addHandler(eventHandler, "ws/event")
                .setAllowedOriginPatterns("*");
    }
}
