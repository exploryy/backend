package com.hits.open.world.core.friend.repository;

import java.util.List;

public interface FriendRepository {
    void createFriend(String userId, String friendId);
    void deleteFriend(String userId, String friendId);
    List<String> getFriends(String userId);
    boolean isFriend(String userId, String friendId);
}
