package com.hits.open.world.core.cosmetic_item;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CosmeticItemService {
    private final CosmeticItemRepository cosmeticItemRepository;

    public List<CosmeticItemEntity> findByName(String name) {
        return cosmeticItemRepository.findByName(name);
    }

    public CosmeticItemEntity findById(Long id) {
        return cosmeticItemRepository.findById(id).orElseThrow();
    }
}
