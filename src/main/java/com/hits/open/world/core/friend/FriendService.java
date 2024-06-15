package com.hits.open.world.core.friend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hits.open.world.core.event.EventService;
import com.hits.open.world.core.event.EventType;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.core.friend.repository.FriendEntity;
import com.hits.open.world.core.friend.repository.FriendRepository;
import com.hits.open.world.core.user.UserEntity;
import com.hits.open.world.client.keycloak.UserClient;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.public_interface.event.EventDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.friend.AllFriendDto;
import com.hits.open.world.public_interface.friend.FriendDto;
import com.hits.open.world.public_interface.friend.RequestFriendsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.partitioningBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserClient userClient;
    private final FileStorageService fileStorageService;
    private final EventService eventService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void addFriendRequest(String userId, String friendId) {
        if (friendRepository.isFriend(userId, friendId)) {
            throw new ExceptionInApplication("Friend already exists", ExceptionType.INVALID);
        }
        if (friendRepository.isFriendRequestExist(userId, friendId) || friendRepository.isFriendRequestExist(friendId, userId)) {
            throw new ExceptionInApplication("Friend request already exists", ExceptionType.INVALID);
        }

        friendRepository.createFriendRequest(userId, friendId);

        var user = userService.getProfile(userId);
        try {
            notifyUser(friendId, user , EventType.REQUEST_TO_FRIEND);
        } catch (Exception e) {
            log.error("Failed to send friend request", e);
        }

    }

    @Transactional
    public void removeFriend(String userId, String friendId) {
        friendRepository.deleteFriend(friendId, userId);
        friendRepository.deleteFriend(userId, friendId);
    }

    @Transactional(readOnly = true)
    public AllFriendDto getFriends(String userId) {
        List<FriendEntity> friends = friendRepository.getFriends(userId);
        Map<Boolean, List<FriendEntity>> friendFavorite = friends.stream()
                .collect(partitioningBy(FriendEntity::isFavorite));

        return new AllFriendDto(
                mapToFriendDto(friendFavorite.get(true).stream().map(FriendEntity::friendId).toList()).toList(),
                mapToFriendDto(friendFavorite.get(false).stream().map(FriendEntity::friendId).toList()).toList()
        );
    }

    @Transactional
    public void acceptFriend(String userId, String friendId) {
        friendRepository.createFriend(userId, friendId);
        friendRepository.createFriend(friendId, userId);
        friendRepository.deleteFriendRequest(friendId, userId);
    }

    @Transactional
    public void declineFriend(String userId, String friendId) {
        friendRepository.deleteFriendRequest(friendId, userId);
    }

    @Transactional
    public void favoriteFriend(String userId, String friendId) {
        var friend = friendRepository.getFriend(userId, friendId)
                .orElseThrow(() -> new ExceptionInApplication("Friend not found", ExceptionType.NOT_FOUND));
        if (friend.isFavorite()) {
            throw new ExceptionInApplication("Friend is already favorite", ExceptionType.INVALID);
        }

        var newFriendEntity = new FriendEntity(
                friend.userId(),
                friend.friendId(),
                true
        );
        friendRepository.updateFavorite(newFriendEntity);
    }

    @Transactional
    public void unfavoriteFriend(String userId, String friendId) {
        var friend = friendRepository.getFriend(userId, friendId)
                .orElseThrow(() -> new ExceptionInApplication("Friend not found", ExceptionType.NOT_FOUND));
        if (!friend.isFavorite()) {
            throw new ExceptionInApplication("Friend is not favorite", ExceptionType.INVALID);
        }

        var newFriendEntity = new FriendEntity(
                friend.userId(),
                friend.friendId(),
                false
        );
        friendRepository.updateFavorite(newFriendEntity);
    }

    @Transactional(readOnly = true)
    public RequestFriendsDto getFriendRequests(String userId) {
        List<String> myFriendRequests = friendRepository.getMyFriendRequests(userId);
        List<String> otherFriendRequests = friendRepository.getOtherFriendRequests(userId);
        return new RequestFriendsDto(
                mapToFriendDto(myFriendRequests).toList(),
                mapToFriendDto(otherFriendRequests).toList()
        );
    }

    private void notifyUser(String friendId, Object message, EventType eventType) {
        try {
            eventService.sendEvent(friendId, new EventDto(message, eventType));
        } catch (Exception e) {
            log.error("Failed to send event to friend", e);
        }
    }

    private FriendDto mapToFriendDto(UserEntity entity) {
        var avatarUrl = fileStorageService.getDownloadLinkByName(entity.getPhotoName());
        return new FriendDto(
                entity.id(),
                entity.username(),
                entity.email(),
                avatarUrl
        );
    }

    private Stream<FriendDto> mapToFriendDto(List<String> usersId) {
        return usersId.stream()
                .map(userClient::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::mapToFriendDto);
    }
}
