package com.hits.open.world.rest.controller.inventory;

import com.hits.open.world.core.inventory.InventoryService;
import com.hits.open.world.core.shop.ShopService;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemInInventoryDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final ShopService shopService;

    @GetMapping
    public List<CosmeticItemInInventoryDto> getInventory(@RequestParam(value = "name", required = false) Optional<String> name,
                                                         JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        return inventoryService.getInventoryItems(userId);
    }

    @DeleteMapping(path = "{item_id}/sell")
    public void sellItem(@PathVariable("item_id") Long itemId,
                         JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        shopService.sellItem(userId, itemId);
    }

    @PostMapping(path = "{item_id}/equip")
    public void equipItem(@PathVariable("item_id") Long itemId,
                          JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        inventoryService.equipItem(userId, itemId);
    }

    @PostMapping(path = "{item_id}/unequip")
    public void unequipItem(@PathVariable("item_id") Long itemId,
                            JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        inventoryService.unequipItem(userId, itemId);
    }
}
