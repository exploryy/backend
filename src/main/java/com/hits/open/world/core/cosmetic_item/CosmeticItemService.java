package com.hits.open.world.core.cosmetic_item;

import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CosmeticItemService {
    private final CosmeticItemRepository cosmeticItemRepository;
    private final FileStorageService fileStorageService;

    public List<CosmeticItemEntity> findByName(String name) {
        return cosmeticItemRepository.findByName(name);
    }

    public Optional<CosmeticItemEntity> findById(Long id) {
        return cosmeticItemRepository.findById(id);
    }

    public String getPhotoUrl(Long id) {
        return fileStorageService.getDownloadLinkByName(getPhotoFileName(id))
                .orElseThrow(() -> new ExceptionInApplication("Photo not found", ExceptionType.NOT_FOUND));
    }

    private String getPhotoFileName(Long id) {
        return "cosmetic_item_%s".formatted(id);
    }
}
