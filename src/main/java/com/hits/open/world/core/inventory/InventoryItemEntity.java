package com.hits.open.world.core.inventory;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticTypeEnum;
import com.hits.open.world.core.cosmetic_item.entity.RarityTypeEnum;

public record InventoryItemEntity(
        Long itemId,
        String name,
        String description,
        int price,
        RarityTypeEnum rarityType,
        CosmeticTypeEnum cosmeticType,
        boolean isEquipped
) {
}
