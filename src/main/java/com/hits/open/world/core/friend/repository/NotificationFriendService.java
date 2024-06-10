package com.hits.open.world.core.friend.repository;

import com.google.gson.Gson;
import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.websocket.client.WebSocketClient;
import com.hits.open.world.public_interface.location.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class NotificationFriendService {
    private static final Gson objectMapper = new Gson();
    private final WebSocketClient webSocketClient;
    private final FriendService friendService;

    public void notifyFriendsAboutNewLocation(LocationDto dto) {
        var userId = dto.clientId();

        var responseMessage = objectMapper.toJson(dto);

        var friends = friendService.getFriends(userId);
        var friend = friends.friends().stream()
                .toList();
        var bestFriend = friends.friends().stream()
                .toList();

        var allFriends = Stream.concat(friend.stream(), bestFriend.stream())
                .toList();

        allFriends.forEach(friendDto -> webSocketClient.sendFriendsPosition(friendDto.userId(), responseMessage));
    }
}
