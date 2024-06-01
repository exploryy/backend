package com.hits.open.world.core.cosmetic_item;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CosmeticItemRepositoryImpl implements CosmeticItemRepository {
    private static final CosmeticItemEntityMapper COSMETIC_ITEM_ENTITY_MAPPER = new CosmeticItemEntityMapper();

    private final DSLContext dslContext;


}
