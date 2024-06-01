package com.hits.open.world.core.battle_pass;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BattlePassRepositoryImpl implements BattlePassRepository {
    private final DSLContext create;

    
}
