package com.hits.open.world.rest.shop;

import com.hits.open.world.core.shop.ShopService;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemInShopDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
public class ShopController {
    private final ShopService shopService;

    @GetMapping
    public List<CosmeticItemInShopDto> getShopItems(@RequestParam(value = "name", required = false) Optional<String> name,
                                                    JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        return shopService.findCosmeticItemsInShop(name.orElse(""), userId);
    }

    @PostMapping(path = "{item_id}/buy")
    public void buyItem(@PathVariable("item_id") Long itemId,
                        JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        shopService.buyItem(userId, itemId);
    }
}
