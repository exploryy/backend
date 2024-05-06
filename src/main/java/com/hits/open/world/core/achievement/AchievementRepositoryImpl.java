package com.hits.open.world.core.achievement;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AchievementRepositoryImpl implements AchievementRepository {
    private final DSLContext create;


}
