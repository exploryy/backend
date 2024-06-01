package com.hits.open.world.core.shop;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticTypeEnum;
import com.hits.open.world.core.cosmetic_item.entity.RarityTypeEnum;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.open_the_world.public_.Tables.CLIENT_ITEM;
import static com.example.open_the_world.public_.Tables.COSMETIC_ITEM;

@Repository
@RequiredArgsConstructor
public class ShopRepositoryImpl implements ShopRepository {
    private final DSLContext create;

    @Override
    public List<CosmeticItemInShopEntity> findCosmeticItemsInShop(String name, String userId) {
        Condition condition = DSL.trueCondition();
        if (!name.isEmpty()) {
            condition = condition.and(COSMETIC_ITEM.NAME.eq(name));
        }

        return create.select()
                .from(COSMETIC_ITEM)
                .rightJoin(CLIENT_ITEM)
                .on(COSMETIC_ITEM.ITEM_ID.eq(CLIENT_ITEM.ITEM_ID).and(CLIENT_ITEM.CLIENT_ID.eq(userId)).and(condition))
                .fetch(record -> new CosmeticItemInShopEntity(
                        record.get(COSMETIC_ITEM.ITEM_ID),
                        record.get(COSMETIC_ITEM.NAME),
                        record.get(COSMETIC_ITEM.DESCRIPTION),
                        record.get(COSMETIC_ITEM.PRICE),
                        RarityTypeEnum.fromString(record.get(COSMETIC_ITEM.RARITY_TYPE)),
                        CosmeticTypeEnum.fromString(record.get(COSMETIC_ITEM.COSMETIC_TYPE)),
                        record.get(CLIENT_ITEM.ITEM_ID) != null
                ));
    }
}
