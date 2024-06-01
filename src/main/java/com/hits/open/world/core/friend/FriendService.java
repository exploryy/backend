package com.hits.open.world.core.friend;

import com.hits.open.world.core.friend.repository.FriendEntity;
import com.hits.open.world.core.friend.repository.FriendRepository;
import com.hits.open.world.core.user.UserEntity;
import com.hits.open.world.keycloak.UserClient;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.friend.AllFriendDto;
import com.hits.open.world.public_interface.friend.FriendDto;
import com.hits.open.world.public_interface.friend.RequestFriendsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.partitioningBy;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserClient userClient;

    @Transactional
    public void addFriendRequest(String userId, String friendId) {
        if (friendRepository.isFriend(userId, friendId)) {
            throw new ExceptionInApplication("Friend already exists", ExceptionType.INVALID);
        }
        if (friendRepository.isFriendRequestExist(userId, friendId) || friendRepository.isFriendRequestExist(friendId, userId)) {
            throw new ExceptionInApplication("Friend request already exists", ExceptionType.INVALID);
        }

        friendRepository.createFriendRequest(userId, friendId);
    }

    @Transactional
    public void removeFriendRequest(String userId, String friendId) {
        friendRepository.deleteFriendRequest(friendId, userId);
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
        friendRepository.deleteFriendRequest(userId, friendId);
    }

    @Transactional
    public void declineFriend(String userId, String friendId) {
        friendRepository.deleteFriendRequest(userId, friendId);
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

    private FriendDto mapToFriendDto(UserEntity entity) {
        return new FriendDto(
                entity.id(),
                entity.username(),
                entity.email()
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
