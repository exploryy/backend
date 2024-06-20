package com.hits.open.world.core.buff.repository.impl;

import com.hits.open.world.core.buff.repository.BuffRepository;
import com.hits.open.world.core.buff.repository.entity.BuffEntity;
import com.hits.open.world.core.buff.repository.mapper.BufEntityMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.tables.Buff.BUFF;

@Repository
@RequiredArgsConstructor
public class BuffRepositoryImpl implements BuffRepository {
    private static final BufEntityMapper mapper = new BufEntityMapper();
    private final DSLContext create;


    @Override
    public Optional<BuffEntity> findByBuffId(Long buffId) {
        return create.selectFrom(BUFF)
                .where(BUFF.BUFF_ID.eq(buffId))
                .fetchOptional(mapper);
    }

    @Override
    public List<BuffEntity> findAll() {
        return create.selectFrom(BUFF)
                .fetch(mapper);
    }

    @Override
    public List<BuffEntity> findAllByLevel(int levelNumber) {
        return create.selectFrom(BUFF)
                .where(BUFF.LEVEL_NUMBER.eq(levelNumber))
                .fetch(mapper);
    }
}
