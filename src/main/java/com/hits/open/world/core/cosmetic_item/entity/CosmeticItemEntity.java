package com.hits.open.world.core.cosmetic_item.entity;

import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemDto;

public record CosmeticItemEntity(
        Long itemId,
        String name,
        String description,
        int price,
        RarityTypeEnum rarityType,
        CosmeticTypeEnum cosmeticType
) {
    public static CosmeticItemDto toDto(CosmeticItemEntity entity, boolean isOwned) {
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
