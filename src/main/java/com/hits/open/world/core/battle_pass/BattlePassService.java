package com.hits.open.world.core.battle_pass;

import com.hits.open.world.core.cosmetic_item.CosmeticItemService;
import com.hits.open.world.core.cosmetic_item.entity.CosmeticItemEntity;
import com.hits.open.world.core.event.EventService;
import com.hits.open.world.core.event.EventType;
import com.hits.open.world.core.inventory.InventoryRepository;
import com.hits.open.world.core.inventory.InventoryService;
import com.hits.open.world.core.money.MoneyService;
import com.hits.open.world.public_interface.battle_pass.BattlePassDto;
import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemDto;
import com.hits.open.world.public_interface.event.EventDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BattlePassService {
    private final BattlePassRepository battlePassRepository;
    private final CosmeticItemService cosmeticItemService;
    private final InventoryService inventoryService;
    private final MoneyService moneyService;
    private final InventoryRepository inventoryRepository;
    private final EventService eventService;

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
        //TODO: убрать после тестирования
        tryInitializeUserInBattlePass(userId);
        var currentBattlePass = battlePassRepository.getCurrentBattlePass()
                .orElseThrow(() -> new ExceptionInApplication("No current battle pass found", ExceptionType.NOT_FOUND));
        var userLevelInBattlePass = battlePassRepository.getUserStatisticInBattlePass(userId, currentBattlePass.battlePassId())
                .orElseThrow(() -> new ExceptionInApplication("User not found in battle pass", ExceptionType.NOT_FOUND));

        var sumExperienceBeforeLevel = battlePassRepository.sumPreviousLevelsExperience(currentBattlePass.battlePassId(), userLevelInBattlePass.level());
        if (countExperience + userLevelInBattlePass.currentExperience() >= currentBattlePass.levels().get(userLevelInBattlePass.level() - 1).experienceNeeded() + sumExperienceBeforeLevel) {
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

    public void tryInitializeUserInBattlePass(String userId) {
        var currentBattlePass = battlePassRepository.getCurrentBattlePass()
                .orElseThrow(() -> new ExceptionInApplication("No current battle pass found", ExceptionType.NOT_FOUND));
        if (battlePassRepository.getUserStatisticInBattlePass(userId, currentBattlePass.battlePassId()).isEmpty()) {
            battlePassRepository.addUserToBattlePass(userId, currentBattlePass.battlePassId());
        }
    }

    public void increaseLevel(String userId, BattlePassEntity currentBattlePass, BattlePassUserStatisticEntity userLevelInBattlePass, int countExperience) {
        var maxLevel = battlePassRepository.getMaxLevel(currentBattlePass.battlePassId());
        var currentUserLevel = userLevelInBattlePass.level();

        if (currentUserLevel <= maxLevel) {
            addItemToInventoryForLevelUp(userId, currentBattlePass, currentUserLevel - 1);
        }
        if (maxLevel != currentUserLevel) {
            eventService.sendEvent(
                    userId,
                    new EventDto(
                            "%d".formatted(currentUserLevel + 1),
                            EventType.UPDATE_BATTLE_PASS_LEVEL
                    )
            );
            battlePassRepository.updateLevelAndExperience(
                    userId,
                    currentBattlePass.battlePassId(),
                    currentUserLevel + 1,
                    userLevelInBattlePass.currentExperience() + countExperience
            );
        }
    }

    private void addItemToInventoryForLevelUp(String userId, BattlePassEntity currentBattlePass, int level) {
        var rewards = currentBattlePass.levels().get(level).rewards();
        for (var reward : rewards) {
            try {
                inventoryService.addItemToInventory(userId, reward.itemId());
            } catch (ExceptionInApplication e) {
                log.error("add item to inventory", e);
                moneyService.addMoney(
                        userId,
                        cosmeticItemService.findById(reward.itemId())
                                .orElseThrow()
                                .price()
                );
            }
        }
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
                    return 2;
                }) - 1,
                userLevelInBattlePass.map(BattlePassUserStatisticEntity::currentExperience).orElse(0),
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
