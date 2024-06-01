package com.hits.open.world.core.cosmetic_item;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntityMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.tables.CosmeticItem.COSMETIC_ITEM;

@Repository
@RequiredArgsConstructor
public class CosmeticItemRepositoryImpl implements CosmeticItemRepository {
    private static final CosmeticItemEntityMapper COSMETIC_ITEM_ENTITY_MAPPER = new CosmeticItemEntityMapper();

    private final DSLContext create;

    @Override
    public List<CosmeticItemEntity> findByName(String name) {
        Condition condition = DSL.trueCondition();
        if (!name.isEmpty()) {
            condition = condition.and(COSMETIC_ITEM.NAME.eq(name));
        }

        return create.selectFrom(COSMETIC_ITEM)
                .where(condition)
                .fetch(COSMETIC_ITEM_ENTITY_MAPPER);
    }

    @Override
    public Optional<CosmeticItemEntity> findById(Long id) {
        return create.selectFrom(COSMETIC_ITEM)
                .where(COSMETIC_ITEM.ITEM_ID.eq(id))
                .fetchOptional(COSMETIC_ITEM_ENTITY_MAPPER);
    }
}
