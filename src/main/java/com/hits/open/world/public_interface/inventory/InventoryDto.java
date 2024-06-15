package com.hits.open.world.public_interface.inventory;

import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemInInventoryDto;

import java.util.Optional;

public record InventoryDto(
        Optional<CosmeticItemInInventoryDto> footprint,
        Optional<CosmeticItemInInventoryDto> avatarFrames,
        Optional<CosmeticItemInInventoryDto> applicationImage,
        Optional<CosmeticItemInInventoryDto> fog
) {
}
