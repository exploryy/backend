package com.hits.open.world.core.shop;

import com.hits.open.world.core.cosmetic_item.CosmeticItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final CosmeticItemService cosmeticItemService;

}
