package com.hits.open.world.core.inventory;

import com.hits.open.world.core.cosmetic_item.CosmeticItemService;
import com.hits.open.world.core.cosmetic_item.entity.CosmeticTypeEnum;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemInInventoryDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final CosmeticItemService cosmeticItemService;

    public List<CosmeticItemInInventoryDto> getInventoryItems(String userId) {
        return inventoryRepository.findByUserId(userId)
                .stream()
                .map(InventoryItemEntity::fromEntity)
                .toList();
    }

    @Transactional
    public void equipItem(String userId, Long itemId) {
        var isItemOwned = inventoryRepository.isItemOwned(userId, itemId);
        if (!isItemOwned) {
            throw new ExceptionInApplication("Item is not owned by user", ExceptionType.NOT_FOUND);
        }
        Map<CosmeticTypeEnum, List<CosmeticItemInInventoryDto>> equippedItems = getInventoryItems(userId).stream()
                .filter(CosmeticItemInInventoryDto::isEquipped)
                .collect(Collectors.groupingBy(CosmeticItemInInventoryDto::cosmeticType));

        var equippedItem = cosmeticItemService.findById(itemId)
                .orElseThrow(() -> new ExceptionInApplication("Item not found", ExceptionType.NOT_FOUND));
        if (equippedItems.containsKey(equippedItem.cosmeticType())) {
            throw new ExceptionInApplication("Item with type %s already equipped".formatted(equippedItem.cosmeticType()), ExceptionType.INVALID);
        }

        inventoryRepository.equipItem(userId, itemId);
    }

    @Transactional
    public void unequipItem(String userId, Long itemId) {
        var isItemEquipped = inventoryRepository.isItemEquipped(userId, itemId);
        if (!isItemEquipped) {
            throw new ExceptionInApplication("Item is not equipped", ExceptionType.INVALID);
        }

        inventoryRepository.unequipItem(userId, itemId);
    }

    @Transactional
    public void addItemToInventory(String userId, Long itemId) {
        var isItemOwned = inventoryRepository.isItemOwned(userId, itemId);
        if (isItemOwned) {
            throw new ExceptionInApplication("Item is already owned by user", ExceptionType.INVALID);
        }

        inventoryRepository.addItem(userId, itemId);
    }

    @Transactional
    public void removeItemFromInventory(String userId, Long itemId) {
        var isItemOwned = inventoryRepository.isItemOwned(userId, itemId);
        if (!isItemOwned) {
            throw new ExceptionInApplication("Item is not owned by user", ExceptionType.NOT_FOUND);
        }

        inventoryRepository.deleteItem(userId, itemId);
    }
}
