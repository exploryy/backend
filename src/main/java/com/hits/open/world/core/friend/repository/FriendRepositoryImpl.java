package com.hits.open.world.core.friend.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.tables.Friend.FRIEND;
import static com.example.open_the_world.public_.tables.FriendRequest.FRIEND_REQUEST;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {
    private static final FriendEntityMapper FRIEND_ENTITY_MAPPER = new FriendEntityMapper();

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
    public List<FriendEntity> getFriends(String userId) {
        return create.selectFrom(FRIEND)
                .where(FRIEND.CLIENT_ID.eq(userId))
                .fetch(FRIEND_ENTITY_MAPPER);
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

    @Override
    public void createFriendRequest(String userId, String friendId) {
        create.insertInto(FRIEND_REQUEST)
                .set(FRIEND_REQUEST.CLIENT_ID, userId)
                .set(FRIEND_REQUEST.FRIEND_ID, friendId)
                .execute();
    }

    @Override
    public void deleteFriendRequest(String userId, String friendId) {
        create.deleteFrom(FRIEND_REQUEST)
                .where(FRIEND_REQUEST.CLIENT_ID.eq(userId))
                .and(FRIEND_REQUEST.FRIEND_ID.eq(friendId))
                .execute();
    }

    @Override
    public void updateFavorite(FriendEntity friendEntity) {
        create.update(FRIEND)
                .set(FRIEND.IS_FAVORITE, friendEntity.isFavorite())
                .where(FRIEND.CLIENT_ID.eq(friendEntity.userId()))
                .and(FRIEND.FRIEND_ID.eq(friendEntity.friendId()))
                .execute();
    }

    @Override
    public Optional<FriendEntity> getFriend(String userId, String friendId) {
        return create.selectFrom(FRIEND)
                .where(FRIEND.CLIENT_ID.eq(userId))
                .and(FRIEND.FRIEND_ID.eq(friendId))
                .fetchOptional(FRIEND_ENTITY_MAPPER);
    }

    @Override
    public List<String> getMyFriendRequests(String userId) {
        return create.select(FRIEND_REQUEST.FRIEND_ID)
                .from(FRIEND_REQUEST)
                .where(FRIEND_REQUEST.CLIENT_ID.eq(userId))
                .fetch(FRIEND_REQUEST.FRIEND_ID);
    }

    @Override
    public List<String> getOtherFriendRequests(String userId) {
        return create.select(FRIEND_REQUEST.CLIENT_ID)
                .from(FRIEND_REQUEST)
                .where(FRIEND_REQUEST.FRIEND_ID.eq(userId))
                .fetch(FRIEND_REQUEST.CLIENT_ID);
    }

    @Override
    public boolean isFriendRequestExist(String userId, String friendId) {
        return create.fetchExists(
                create.selectOne()
                        .from(FRIEND_REQUEST)
                        .where(FRIEND_REQUEST.CLIENT_ID.eq(userId))
                        .and(FRIEND_REQUEST.FRIEND_ID.eq(friendId))
        );
    }
}
