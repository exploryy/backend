package com.hits.open.world.core.friend;

import com.hits.open.world.core.user.UserEntity;
import com.hits.open.world.core.friend.repository.FriendRepository;
import com.hits.open.world.keycloak.UserClient;
import com.hits.open.world.public_interface.friend.FriendDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserClient userClient;

    @Transactional
    public void addFriend(String userId, String friendId) {
        friendRepository.createFriend(userId, friendId);
    }

    @Transactional
    public void removeFriend(String userId, String friendId) {
        friendRepository.deleteFriend(userId, friendId);
    }

    @Transactional(readOnly = true)
    public List<FriendDto> getFriends(String userId) {
        var friendIds = friendRepository.getFriends(userId);
        return friendIds.stream()
                .map(userClient::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::mapToFriendDto)
                .toList();
    }

    private FriendDto mapToFriendDto(UserEntity entity) {
        return new FriendDto(
                entity.id(),
                entity.username(),
                entity.email()
        );
    }
}
