package com.hits.open.world.core.quest.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestRepositoryImpl implements QuestRepository {
    private final DSLContext create;


}
