package com.hits.open.world.public_interface.user;

import com.hits.open.world.public_interface.inventory.InventoryDto;

import java.util.Optional;

public record ProfileDto(
        String userId,
        String username,
        String email,
        Optional<String> avatarUrl,
        InventoryDto inventoryDto
) {
}
