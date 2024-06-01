package com.hits.open.world.core.cosmetic_item.entity;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;

public enum CosmeticTypeEnum {
    FOOTPRINT,
    AVATAR_FRAMES,
    APPLICATION_IMAGE,
    FOG,
    ;

    public static CosmeticTypeEnum fromString(String cosmeticType) {
        try {
            return CosmeticTypeEnum.valueOf(cosmeticType);
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Invalid cosmetic type: " + cosmeticType, ExceptionType.INVALID);
        }
    }
}
