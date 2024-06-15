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
        boolean isOwned,
        boolean sellable,
        String url
) {
}
