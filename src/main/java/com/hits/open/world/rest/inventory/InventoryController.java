package com.hits.open.world.rest.inventory;

import com.hits.open.world.core.inventory.InventoryService;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@RequestMapping("/shop")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Shop")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public List<CosmeticItemDto> getShopItems(@RequestParam(value = "name",required = false) Optional<String> name,
                                              JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();

    }

    @DeleteMapping(path = "{item_id}/sell", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sellItem(@PathVariable("item_id") Long itemId,
                        JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();

    }

    @PostMapping(path = "{item_id}/equip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void equipItem(@PathVariable("item_id") Long itemId,
                         JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();

    }

    @PostMapping(path = "{item_id}/unequip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void unequipItem(@PathVariable("item_id") Long itemId,
                         JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();

    }
}
