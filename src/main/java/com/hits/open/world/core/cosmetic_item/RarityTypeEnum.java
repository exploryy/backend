package com.hits.open.world.core.cosmetic_item;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;

public enum RarityTypeEnum {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY,
    ;

    public static RarityTypeEnum fromString(String rarity) {
        try {
            return RarityTypeEnum.valueOf(rarity);
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Invalid rarity type: " + rarity, ExceptionType.INVALID);
        }
    }
}
