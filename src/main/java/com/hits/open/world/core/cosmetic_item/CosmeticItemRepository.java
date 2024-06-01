package com.hits.open.world.core.cosmetic_item;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;

import java.util.List;
import java.util.Optional;

public interface CosmeticItemRepository {
    List<CosmeticItemEntity> findByName(String name);

    Optional<CosmeticItemEntity> findById(Long id);
}
