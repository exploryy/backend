package com.hits.open.world.core.friend.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.open_the_world.public_.tables.Friend.FRIEND;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {
    private final DSLContext create;

    @Override
    public void createFriend(String userId, String friendId) {
        create.insertInto(FRIEND)
                .set(FRIEND.CLIENT_ID, userId)
                .set(FRIEND.FRIEND_ID, friendId)
                .execute();
    }

    @Override
    public void deleteFriend(String userId, String friendId) {
        create.deleteFrom(FRIEND)
                .where(FRIEND.CLIENT_ID.eq(userId))
                .and(FRIEND.FRIEND_ID.eq(friendId))
                .execute();
    }

    @Override
    public List<String> getFriendIds(String userId) {
        return create.select(FRIEND.FRIEND_ID)
                .from(FRIEND)
                .where(FRIEND.CLIENT_ID.eq(userId))
                .fetch(FRIEND.FRIEND_ID);
    }

    @Override
    public boolean isFriend(String userId, String friendId) {
        return create.fetchExists(
                create.selectOne()
                        .from(FRIEND)
                        .where(FRIEND.CLIENT_ID.eq(userId))
                        .and(FRIEND.FRIEND_ID.eq(friendId))
        );
    }
}
