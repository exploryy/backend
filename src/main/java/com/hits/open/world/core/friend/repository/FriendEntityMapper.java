package com.hits.open.world.core.friend.repository;

import com.example.open_the_world.public_.tables.records.FriendRecord;
import org.jooq.RecordMapper;

public class FriendEntityMapper implements RecordMapper<FriendRecord, FriendEntity> {
    @Override
    public FriendEntity map(FriendRecord friendRecord) {
        return new FriendEntity(
                friendRecord.getClientId(),
                friendRecord.getFriendId(),
                friendRecord.getIsFavorite()
        );
    }
}
