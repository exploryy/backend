package com.hits.open.world.core.shop;

import java.util.List;

public interface ShopRepository {
    List<CosmeticItemInShopEntity> findCosmeticItemsInShop(String name, String userId);
}
