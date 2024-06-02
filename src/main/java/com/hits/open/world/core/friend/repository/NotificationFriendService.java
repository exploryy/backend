package com.hits.open.world.core.friend.repository;

import com.google.gson.Gson;
import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.location.repository.UserLocationEntity;
import com.hits.open.world.core.websocket.WebSocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationFriendService {
    private static final Gson objectMapper = new Gson();
    private final WebSocketClient webSocketClient;
    private final FriendService friendService;

    public void notifyFriendsAboutNewLocation(UserLocationEntity entity) {
        var userId = entity.clientId();
        var responseMessage = objectMapper.toJson(entity);

        var friends = friendService.getFriends(userId);
        var allFriends = friends.friends();

        allFriends.forEach(friendDto -> webSocketClient.sendFriendsPosition(friendDto.userId(), responseMessage));
    }
}
