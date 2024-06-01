package com.hits.open.world.core.inventory;

import java.util.List;

public interface InventoryRepository {
    List<InventoryItemEntity> findByUserId(String userId);

    void equipItem(String userId, Long itemId);

    void unequipItem(String userId, Long itemId);

    void deleteItem(String userId, Long itemId);

    void addItem(String userId, Long itemId);

    boolean isItemOwned(String userId, Long itemId);

    boolean isItemEquipped(String userId, Long itemId);
}
