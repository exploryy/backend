package com.hits.open.world.core.quest;

import com.hits.open.world.core.quest.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;


}