package com.hits.open.world.core.friend.repository;

import com.google.gson.Gson;
import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.websocket.client.WebSocketClient;
import com.hits.open.world.public_interface.multipolygon.PolygonRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class NotificationFriendService {
    private static final Gson objectMapper = new Gson();
    private final WebSocketClient webSocketClient;
    private final FriendService friendService;

    public void notifyFriendsAboutNewLocation(PolygonRequestDto dto) {
        var userId = dto.userId();
        var responseMessage = objectMapper.toJson(dto);

        var friends = friendService.getFriends(userId);
        var allFriends = Stream.concat(
                friends.friends().stream(),
                friends.favoriteFriends().stream()
        ).distinct().toList();

        allFriends.forEach(friendDto ->
                webSocketClient.sendFriendsPosition(friendDto.userId(), responseMessage)
        );
    }
}
