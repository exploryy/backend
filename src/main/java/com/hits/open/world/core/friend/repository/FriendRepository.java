package com.hits.open.world.core.friend.repository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository {
    void createFriend(String userId, String friendId);
    void deleteFriend(String userId, String friendId);
    List<FriendEntity> getFriends(String userId);
    boolean isFriend(String userId, String friendId);
    void createFriendRequest(String userId, String friendId);
    void deleteFriendRequest(String userId, String friendId);
    void updateFavorite(FriendEntity friendEntity);
    Optional<FriendEntity> getFriend(String userId, String friendId);
    List<String> getMyFriendRequests(String userId);
    List<String> getOtherFriendRequests(String userId);
    boolean isFriendRequestExist(String userId, String friendId);
}
