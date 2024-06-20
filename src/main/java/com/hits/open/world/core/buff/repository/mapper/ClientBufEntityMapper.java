package com.hits.open.world.core.buff.repository.mapper;

import com.example.open_the_world.public_.tables.records.ClientBuffRecord;
import com.hits.open.world.core.buff.repository.entity.ClientBufEntity;
import org.jooq.RecordMapper;

public class ClientBufEntityMapper implements RecordMapper<ClientBuffRecord, ClientBufEntity> {

    @Override
    public ClientBufEntity map(ClientBuffRecord clientBuffRecord) {
        return new ClientBufEntity(
                clientBuffRecord.getClientId(),
                clientBuffRecord.getBuffId()
        );
    }
}
