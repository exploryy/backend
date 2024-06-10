package com.hits.open.world.core.friend.repository;

import com.google.gson.Gson;
import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.core.websocket.client.WebSocketClient;
import com.hits.open.world.public_interface.location.FriendLocationDto;
import com.hits.open.world.public_interface.location.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationFriendService {
    private static final Gson objectMapper = new Gson();
    private final WebSocketClient webSocketClient;
    private final FriendService friendService;
    private final UserService userService;

    public void notifyFriendsAboutNewLocation(LocationDto dto) {
        var userId = dto.clientId();

        var userProfile = userService.getProfile(userId);

        var friendLocationDto = FriendLocationDto.builder()
                .username(userProfile.username())
                .userId(userId)
                .email(userProfile.email())
                .avatarUrl(userProfile.avatarUrl())
                .longitude(dto.longitude())
                .latitude(dto.latitude())
                .build();

        var responseMessage = objectMapper.toJson(friendLocationDto);

        var friends = friendService.getFriends(userId);
        var allFriends = friends.friends();

        allFriends.forEach(friendDto -> webSocketClient.sendFriendsPosition(friendDto.userId(), responseMessage));
    }
}
