package com.hits.open.world.core.friend.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {
    private final DSLContext create;

    @Override
    public void createFriend(String userId, String friendId) {

    }

    @Override
    public void deleteFriend(String userId, String friendId) {

    }

    @Override
    public List<String> getFriends(String userId) {
        return null;
    }

    @Override
    public boolean isFriend(String userId, String friendId) {
        return false;
    }
}
