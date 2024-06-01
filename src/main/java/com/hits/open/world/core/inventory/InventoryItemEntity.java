package com.hits.open.world.core.inventory;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import com.hits.open.world.core.cosmetic_item.entity.CosmeticTypeEnum;
import com.hits.open.world.core.cosmetic_item.entity.RarityTypeEnum;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemDto;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemInInventoryDto;

public record InventoryItemEntity(
        Long itemId,
        String name,
        String description,
        int price,
        RarityTypeEnum rarityType,
        CosmeticTypeEnum cosmeticType,
        boolean isEquipped
) {
    public static CosmeticItemInInventoryDto fromEntity(InventoryItemEntity entity) {
        return new CosmeticItemInInventoryDto(
                entity.itemId(),
                entity.name(),
                entity.description(),
                entity.price(),
                entity.rarityType(),
                entity.cosmeticType(),
                entity.isEquipped()
        );
    }
}
