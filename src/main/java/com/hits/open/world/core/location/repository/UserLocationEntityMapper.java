package com.hits.open.world.core.location.repository;

import com.example.open_the_world.public_.tables.records.UserLocationRecord;
import org.jooq.RecordMapper;

public class UserLocationEntityMapper implements RecordMapper<UserLocationRecord, UserLocationEntity> {

    @Override
    public UserLocationEntity map(UserLocationRecord userLocationRecord) {
        return new UserLocationEntity(
                userLocationRecord.getClientId(),
                userLocationRecord.getLatitude(),
                userLocationRecord.getLongitude(),
                userLocationRecord.getLastVisitation()
        );
    }
}
