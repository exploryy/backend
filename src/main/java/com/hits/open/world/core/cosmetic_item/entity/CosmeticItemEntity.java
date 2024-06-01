package com.hits.open.world.core.cosmetic_item.entity;

public record CosmeticItemEntity(
        Long itemId,
        String name,
        String description,
        int price,
        RarityTypeEnum rarityType,
        CosmeticTypeEnum cosmeticType
) {
}
