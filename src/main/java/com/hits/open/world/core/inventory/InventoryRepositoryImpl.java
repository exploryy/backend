package com.hits.open.world.core.inventory;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticTypeEnum;
import com.hits.open.world.core.cosmetic_item.entity.RarityTypeEnum;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.open_the_world.public_.Tables.CLIENT_ITEM;
import static com.example.open_the_world.public_.Tables.COSMETIC_ITEM;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {
    private final DSLContext create;

    @Override
    public List<InventoryItemEntity> findByUserId(String userId) {
        return create.select(COSMETIC_ITEM.ITEM_ID, COSMETIC_ITEM.NAME, COSMETIC_ITEM.DESCRIPTION, COSMETIC_ITEM.PRICE, COSMETIC_ITEM.RARITY_TYPE, COSMETIC_ITEM.COSMETIC_TYPE, CLIENT_ITEM.IS_EQUIPPED)
                .from(CLIENT_ITEM)
                .rightJoin(COSMETIC_ITEM)
                .on(CLIENT_ITEM.ITEM_ID.eq(COSMETIC_ITEM.ITEM_ID).and(CLIENT_ITEM.CLIENT_ID.eq(userId)))
                .fetch(record -> new InventoryItemEntity(
                        record.get(COSMETIC_ITEM.ITEM_ID),
                        record.get(COSMETIC_ITEM.NAME),
                        record.get(COSMETIC_ITEM.DESCRIPTION),
                        record.get(COSMETIC_ITEM.PRICE),
                        RarityTypeEnum.fromString(record.get(COSMETIC_ITEM.RARITY_TYPE)),
                        CosmeticTypeEnum.fromString(record.get(COSMETIC_ITEM.COSMETIC_TYPE)),
                        record.get(CLIENT_ITEM.IS_EQUIPPED)
                ));
    }

    @Override
    public void equipItem(String userId, Long itemId) {
        create.update(CLIENT_ITEM)
                .set(CLIENT_ITEM.IS_EQUIPPED, true)
                .where(CLIENT_ITEM.CLIENT_ID.eq(userId).and(CLIENT_ITEM.ITEM_ID.eq(itemId)))
                .execute();
    }

    @Override
    public void unequipItem(String userId, Long itemId) {
        create.update(CLIENT_ITEM)
                .set(CLIENT_ITEM.IS_EQUIPPED, false)
                .where(CLIENT_ITEM.CLIENT_ID.eq(userId).and(CLIENT_ITEM.ITEM_ID.eq(itemId)))
                .execute();
    }

    @Override
    public void deleteItem(String userId, Long itemId) {
        create.deleteFrom(CLIENT_ITEM)
                .where(CLIENT_ITEM.CLIENT_ID.eq(userId).and(CLIENT_ITEM.ITEM_ID.eq(itemId)))
                .execute();
    }

    @Override
    public void addItem(String userId, Long itemId) {
        create.insertInto(CLIENT_ITEM)
                .set(CLIENT_ITEM.CLIENT_ID, userId)
                .set(CLIENT_ITEM.ITEM_ID, itemId)
                .set(CLIENT_ITEM.IS_EQUIPPED, false)
                .execute();
    }

    @Override
    public boolean isItemOwned(String userId, Long itemId) {
        return create.fetchExists(CLIENT_ITEM, CLIENT_ITEM.CLIENT_ID.eq(userId).and(CLIENT_ITEM.ITEM_ID.eq(itemId)));
    }

    @Override
    public boolean isItemEquipped(String userId, Long itemId) {
        return create.fetchExists(CLIENT_ITEM, CLIENT_ITEM.CLIENT_ID.eq(userId).and(CLIENT_ITEM.ITEM_ID.eq(itemId)).and(CLIENT_ITEM.IS_EQUIPPED.isTrue()));
    }
}
