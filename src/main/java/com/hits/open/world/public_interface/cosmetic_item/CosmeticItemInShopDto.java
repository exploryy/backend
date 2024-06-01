package com.hits.open.world.public_interface.cosmetic_item;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticTypeEnum;
import com.hits.open.world.core.cosmetic_item.entity.RarityTypeEnum;
import com.hits.open.world.core.shop.CosmeticItemInShopEntity;

public record CosmeticItemInShopDto(
        Long itemId,
        String name,
        String description,
        int price,
        RarityTypeEnum rarityType,
        CosmeticTypeEnum cosmeticType,
        boolean isOwned
) {
    public static CosmeticItemInShopDto fromEntity(CosmeticItemInShopEntity entity) {
        return new CosmeticItemInShopDto(
                entity.itemId(),
                entity.name(),
                entity.description(),
                entity.price(),
                entity.rarityType(),
                entity.cosmeticType(),
                entity.isOwned()
        );
    }
}
