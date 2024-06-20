package com.hits.open.world.core.buff.repository.impl;

import com.hits.open.world.core.buff.repository.ClientBufRepository;
import com.hits.open.world.core.buff.repository.entity.ClientBufEntity;
import com.hits.open.world.core.buff.repository.mapper.ClientBufEntityMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.tables.ClientBuff.CLIENT_BUFF;

@Repository
@RequiredArgsConstructor
public class ClientBufRepositoryImpl implements ClientBufRepository {
    private static final ClientBufEntityMapper CLIENT_BUF_ENTITY_MAPPER = new ClientBufEntityMapper();
    private final DSLContext create;


    @Override
    public List<ClientBufEntity> findBuffsByUserId(String userId) {
        return create.selectFrom(CLIENT_BUFF)
                .where(CLIENT_BUFF.CLIENT_ID.eq(userId))
                .fetch(CLIENT_BUF_ENTITY_MAPPER);
    }

    @Override
    public Optional<ClientBufEntity> findByBuffIdAndUserId(Long buffId, String userId) {
        return create.selectFrom(CLIENT_BUFF)
                .where(CLIENT_BUFF.BUFF_ID.eq(buffId).and(CLIENT_BUFF.CLIENT_ID.eq(userId)))
                .fetchOptional(CLIENT_BUF_ENTITY_MAPPER);
    }

    @Override
    public void save(ClientBufEntity clientBufEntity) {
        create.insertInto(CLIENT_BUFF)
                .set(CLIENT_BUFF.BUFF_ID, clientBufEntity.buffId())
                .set(CLIENT_BUFF.CLIENT_ID, clientBufEntity.clientId())
                .execute();
    }
}
