package com.hits.open.world.core.cosmetic_item;

import com.example.open_the_world.public_.tables.records.CosmeticItemRecord;
import org.jooq.RecordMapper;

public class CosmeticItemEntityMapper implements RecordMapper<CosmeticItemRecord, CosmeticItemEntity> {
    @Override
    public CosmeticItemEntity map(CosmeticItemRecord cosmeticItemRecord) {
        return new CosmeticItemEntity(
                cosmeticItemRecord.getItemId(),
                cosmeticItemRecord.getName(),
                cosmeticItemRecord.getDescription(),
                cosmeticItemRecord.getPrice(),
                RarityTypeEnum.fromString(cosmeticItemRecord.getRarityType()),
                CosmeticTypeEnum.fromString(cosmeticItemRecord.getCosmeticType())
        );
    }
}
