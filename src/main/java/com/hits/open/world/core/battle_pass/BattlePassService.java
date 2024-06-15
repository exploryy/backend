package com.hits.open.world.core.battle_pass;

import com.hits.open.world.core.cosmetic_item.CosmeticItemService;
import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import com.hits.open.world.core.inventory.InventoryRepository;
import com.hits.open.world.core.inventory.InventoryService;
import com.hits.open.world.core.money.MoneyService;
import com.hits.open.world.public_interface.battle_pass.BattlePassDto;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BattlePassService {
    private final BattlePassRepository battlePassRepository;
    private final CosmeticItemService cosmeticItemService;
    private final InventoryService inventoryService;
    private final MoneyService moneyService;
    private final InventoryRepository inventoryRepository;

    public BattlePassDto getCurrentBattlePass(String userId) {
        var currentBattlePass = battlePassRepository.getCurrentBattlePass()
                .orElseThrow(() -> new ExceptionInApplication("No current battle pass found", ExceptionType.NOT_FOUND));
        return fromEntityToDto(currentBattlePass, userId);
    }

    public List<BattlePassDto> getAllBattlePasses(String userId) {
        return battlePassRepository.getAllBattlePasses().stream()
                .map(battlePassEntity -> fromEntityToDto(battlePassEntity, userId))
                .toList();
    }

    @Transactional
    public void addExperience(String userId, int countExperience) {
        var currentBattlePass = battlePassRepository.getCurrentBattlePass()
                .orElseThrow(() -> new ExceptionInApplication("No current battle pass found", ExceptionType.NOT_FOUND));
        var userLevelInBattlePass = battlePassRepository.getUserStatisticInBattlePass(userId, currentBattlePass.battlePassId())
                .orElseThrow(() -> new ExceptionInApplication("User not found in battle pass", ExceptionType.NOT_FOUND));

        var sumExperienceBeforeLevel = battlePassRepository.sumPreviousLevelsExperience(currentBattlePass.battlePassId(), userLevelInBattlePass.level());
        if (countExperience + sumExperienceBeforeLevel >= currentBattlePass.levels().get(userLevelInBattlePass.level()).experienceNeeded() + sumExperienceBeforeLevel) {
            increaseLevel(userId, currentBattlePass, userLevelInBattlePass, countExperience);
        } else {
            battlePassRepository.updateLevelAndExperience(
                    userId,
                    currentBattlePass.battlePassId(),
                    userLevelInBattlePass.level(),
                    countExperience + userLevelInBattlePass.currentExperience()
            );
        }
    }

    private void increaseLevel(String userId, BattlePassEntity currentBattlePass, BattlePassUserStatisticEntity userLevelInBattlePass, int countExperience) {
        var needIncreaseLevel = battlePassRepository.getMaxLevel(currentBattlePass.battlePassId()) == userLevelInBattlePass.level();
        if (needIncreaseLevel) {
            var rewards = currentBattlePass.levels().get(userLevelInBattlePass.level()).rewards();
            for (var reward : rewards) {
                try {
                    inventoryService.addItemToInventory(userId, reward.itemId());
                } catch (ExceptionInApplication e) {
                    moneyService.addMoney(userId, cosmeticItemService.findById(reward.itemId()).get().price());
                }
            }
        }
        battlePassRepository.updateLevelAndExperience(
                userId,
                currentBattlePass.battlePassId(),
                needIncreaseLevel ? userLevelInBattlePass.level() : userLevelInBattlePass.level() + 1,
                userLevelInBattlePass.currentExperience() + countExperience
        );
    }

    private BattlePassDto fromEntityToDto(BattlePassEntity battlePassEntity, String userId) {
        var userLevelInBattlePass = battlePassRepository.getUserStatisticInBattlePass(userId, battlePassEntity.battlePassId());
        return new BattlePassDto(
                battlePassEntity.battlePassId(),
                battlePassEntity.name(),
                battlePassEntity.description(),
                battlePassEntity.startDate(),
                battlePassEntity.endDate(),
                userLevelInBattlePass.map(BattlePassUserStatisticEntity::level).orElseGet(() -> {
                    battlePassRepository.addUserToBattlePass(userId, battlePassEntity.battlePassId());
                    return 0;
                }),
                battlePassEntity.levels().stream()
                        .map(level -> new BattlePassDto.BattlePassLevelDto(
                                level.level(),
                                level.experienceNeeded(),
                                level.rewards().stream()
                                        .map(reward -> new BattlePassDto.BattlePassLevelDto.BattlePassRewardDto(
                                                cosmeticItemService.findById(reward.itemId())
                                                        .map(entity -> toDto(entity, inventoryRepository.isItemOwned(userId, entity.itemId())))
                                                        .orElseThrow(
                                                                () -> new ExceptionInApplication("Cosmetic item not found", ExceptionType.NOT_FOUND)
                                                        )
                                        ))
                                        .toList()
                        ))
                        .toList()
        );

    }

    private CosmeticItemDto toDto(CosmeticItemEntity entity, boolean isOwned) {
        return new CosmeticItemDto(
                entity.itemId(),
                entity.name(),
                entity.description(),
                entity.price(),
                entity.rarityType(),
                entity.cosmeticType(),
                isOwned,
                entity.sellable(),
                cosmeticItemService.getPhotoUrl(entity.itemId())
        );
    }
}
