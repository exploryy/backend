package com.hits.open.world.core.shop;

import com.hits.open.world.core.cosmetic_item.CosmeticItemService;
import com.hits.open.world.core.inventory.InventoryService;
import com.hits.open.world.core.money.MoneyService;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemInShopDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    private final CosmeticItemService cosmeticItemService;
    private final MoneyService moneyService;
    private final InventoryService inventoryService;

    @Transactional
    public void buyItem(String userId, Long itemId) {
        var item = cosmeticItemService.findById(itemId)
                .orElseThrow(() -> new ExceptionInApplication("Item not found", ExceptionType.NOT_FOUND));
        if (!item.sellable()) {
            throw new ExceptionInApplication("Item is not sellable", ExceptionType.INVALID);
        }

        var price = item.price();
        var userMoney = moneyService.getUserMoney(userId);
        if (userMoney < price) {
            throw new ExceptionInApplication("Not enough money", ExceptionType.INVALID);
        }
        moneyService.subtractMoney(userId, price);
        inventoryService.addItemToInventory(userId, itemId);
    }

    @Transactional
    public void sellItem(String userId, Long itemId) {
        var item = cosmeticItemService.findById(itemId)
                .orElseThrow(() -> new ExceptionInApplication("Item not found", ExceptionType.NOT_FOUND));
        if (!item.sellable()) {
            throw new ExceptionInApplication("Item is not sellable", ExceptionType.INVALID);
        }

        var price = item.price();
        inventoryService.removeItemFromInventory(userId, itemId);
        moneyService.addMoney(userId, price);
    }

    public List<CosmeticItemInShopDto> findCosmeticItemsInShop(String name, String userId) {
        return shopRepository.findCosmeticItemsInShop(name, userId).stream()
                .map(this::toDto)
                .toList();
    }

    private CosmeticItemInShopDto toDto(CosmeticItemInShopEntity entity) {
        return new CosmeticItemInShopDto(
                entity.itemId(),
                entity.name(),
                entity.description(),
                entity.price(),
                entity.rarityType(),
                entity.cosmeticType(),
                entity.isOwned(),
                entity.sellable(),
                cosmeticItemService.getPhotoUrl(entity.itemId())
        );
    }
}
