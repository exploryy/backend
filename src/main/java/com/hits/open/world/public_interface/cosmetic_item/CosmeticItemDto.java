package com.hits.open.world.public_interface.cosmetic_item;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import com.hits.open.world.core.cosmetic_item.entity.CosmeticTypeEnum;
import com.hits.open.world.core.cosmetic_item.entity.RarityTypeEnum;

public record CosmeticItemDto(
        Long itemId,
        String name,
        String description,
        int price,
        RarityTypeEnum rarityType,
        CosmeticTypeEnum cosmeticType,
        boolean isOwned
) {
    public static CosmeticItemDto fromEntity(CosmeticItemEntity entity, boolean isOwned) {
        return new CosmeticItemDto(
                entity.itemId(),
                entity.name(),
                entity.description(),
                entity.price(),
                entity.rarityType(),
                entity.cosmeticType(),
                isOwned
        );
    }
}
