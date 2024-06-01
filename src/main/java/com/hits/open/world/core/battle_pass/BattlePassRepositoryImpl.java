package com.hits.open.world.core.battle_pass;

import com.example.open_the_world.public_.tables.records.BattlePassRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.Tables.BATTLE_PASS;
import static com.example.open_the_world.public_.Tables.BATTLE_PASS_LEVEL;
import static com.example.open_the_world.public_.Tables.CLIENT_BATTLE_PASS;
import static com.example.open_the_world.public_.Tables.ITEM_BATTLE_PASS_LEVEL;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.sum;

@Repository
@RequiredArgsConstructor
public class BattlePassRepositoryImpl implements BattlePassRepository {
    private final DSLContext create;

    @Override
    public Optional<BattlePassEntity> getBattlePass(Long battlePassId) {
        return create.selectFrom(BATTLE_PASS)
                .where(BATTLE_PASS.BATTLE_PASS_ID.eq(battlePassId))
                .fetchOptional(battlePassRecordMapper());
    }

    @Override
    public Optional<BattlePassEntity> getCurrentBattlePass() {
        return create.selectFrom(BATTLE_PASS)
                .where(BATTLE_PASS.START_DATE.lessOrEqual(LocalDateTime.now())
                        .and(BATTLE_PASS.END_DATE.greaterOrEqual(LocalDateTime.now())))
                .fetchOptional(battlePassRecordMapper());
    }

    @Override
    public Optional<BattlePassUserStatisticEntity> getUserStatisticInBattlePass(String userId, Long battlePassId) {
        return create.selectFrom(CLIENT_BATTLE_PASS)
                .where(CLIENT_BATTLE_PASS.CLIENT_ID.eq(userId)
                        .and(CLIENT_BATTLE_PASS.BATTLE_PASS_ID.eq(battlePassId)))
                .fetchOptional(record -> new BattlePassUserStatisticEntity(
                        battlePassId,
                        userId,
                        record.get(CLIENT_BATTLE_PASS.LEVEL),
                        record.get(CLIENT_BATTLE_PASS.CURRENT_EXPERIENCE)
                ));
    }

    @Override
    public void addUserToBattlePass(String userId, Long battlePassId) {
        create.insertInto(CLIENT_BATTLE_PASS)
                .set(CLIENT_BATTLE_PASS.CLIENT_ID, userId)
                .set(CLIENT_BATTLE_PASS.BATTLE_PASS_ID, battlePassId)
                .set(CLIENT_BATTLE_PASS.LEVEL, 0)
                .set(CLIENT_BATTLE_PASS.CURRENT_BATTLE_PASS, true)
                .set(CLIENT_BATTLE_PASS.CURRENT_EXPERIENCE, 0)
                .execute();
    }

    @Override
    public List<BattlePassEntity> getAllBattlePasses() {
        return create.selectFrom(BATTLE_PASS)
                .fetch(battlePassRecordMapper());
    }

    @Override
    public int sumPreviousLevelsExperience(Long battlePassId, int level) {
        return create.select(sum(BATTLE_PASS_LEVEL.EXPERIENCE))
                .from(BATTLE_PASS_LEVEL)
                .where(BATTLE_PASS_LEVEL.BATTLE_PASS_ID.eq(battlePassId)
                        .and(BATTLE_PASS_LEVEL.LEVEL.lessThan(level)))
                .groupBy(BATTLE_PASS_LEVEL.BATTLE_PASS_ID)
                .fetchOptionalInto(Integer.class)
                .orElse(0);
    }

    @Override
    public void updateLevelAndExperience(String userId, Long battlePassId, int level, int experience) {
        create.update(CLIENT_BATTLE_PASS)
                .set(CLIENT_BATTLE_PASS.LEVEL, level)
                .set(CLIENT_BATTLE_PASS.CURRENT_EXPERIENCE, experience)
                .where(CLIENT_BATTLE_PASS.CLIENT_ID.eq(userId)
                        .and(CLIENT_BATTLE_PASS.BATTLE_PASS_ID.eq(battlePassId)))
                .execute();
    }

    @Override
    public int getMaxLevel(Long battlePassId) {
        return create.select(max(BATTLE_PASS_LEVEL.LEVEL))
                .from(BATTLE_PASS_LEVEL)
                .where(BATTLE_PASS_LEVEL.BATTLE_PASS_ID.eq(battlePassId))
                .fetchOptionalInto(Integer.class)
                .orElse(0);
    }

    private RecordMapper<BattlePassRecord, BattlePassEntity> battlePassRecordMapper() {
        return record -> new BattlePassEntity(
                record.get(BATTLE_PASS.BATTLE_PASS_ID),
                record.get(BATTLE_PASS.NAME),
                record.get(BATTLE_PASS.DESCRIPTION),
                record.get(BATTLE_PASS.START_DATE),
                record.get(BATTLE_PASS.END_DATE),
                create.selectFrom(BATTLE_PASS_LEVEL)
                        .where(BATTLE_PASS_LEVEL.BATTLE_PASS_ID.eq(record.get(BATTLE_PASS.BATTLE_PASS_ID)))
                        .fetch(record1 -> new BattlePassEntity.BattlePassLevel(
                                record1.get(BATTLE_PASS_LEVEL.BATTLE_PASS_ID),
                                record1.get(BATTLE_PASS_LEVEL.LEVEL),
                                record1.get(BATTLE_PASS_LEVEL.EXPERIENCE),
                                create.selectFrom(ITEM_BATTLE_PASS_LEVEL)
                                        .where(ITEM_BATTLE_PASS_LEVEL.BATTLE_PASS_ID.eq(record1.get(BATTLE_PASS_LEVEL.BATTLE_PASS_ID))
                                                .and(ITEM_BATTLE_PASS_LEVEL.LEVEL.eq(record1.get(BATTLE_PASS_LEVEL.LEVEL))))
                                        .fetch(record2 -> new BattlePassEntity.BattlePassLevel.BattlePassReward(
                                                record2.get(ITEM_BATTLE_PASS_LEVEL.BATTLE_PASS_ID),
                                                record2.get(ITEM_BATTLE_PASS_LEVEL.LEVEL),
                                                record2.get(ITEM_BATTLE_PASS_LEVEL.ITEM_ID)
                                        ))
                        ))
        );
    }
}
