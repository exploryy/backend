package com.hits.open.world.core.buff.repository.mapper;

import com.example.open_the_world.public_.tables.records.BuffsRecord;
import com.hits.open.world.core.buff.repository.entity.BuffEntity;
import com.hits.open.world.core.buff.repository.enums.BuffStatus;
import org.jooq.RecordMapper;

import java.math.BigDecimal;

public class BufEntityMapper implements RecordMapper<BuffsRecord, BuffEntity> {

    @Override
    public BuffEntity map(BuffsRecord buffRecord) {
        return new BuffEntity(
                buffRecord.getBuffId(),
                BigDecimal.valueOf(buffRecord.getValueFactor()),
                BuffStatus.fromString(buffRecord.getStatus()),
                buffRecord.getLevelNumber()
        );
    }
}
