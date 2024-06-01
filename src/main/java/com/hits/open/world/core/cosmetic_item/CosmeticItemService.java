package com.hits.open.world.core.cosmetic_item;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CosmeticItemService {
    private final CosmeticItemRepository cosmeticItemRepository;

    public List<CosmeticItemEntity> findByName(String name) {
        return cosmeticItemRepository.findByName(name);
    }

    public Optional<CosmeticItemEntity> findById(Long id) {
        return cosmeticItemRepository.findById(id);
    }
}
