package com.hits.open.world.core.note;

import java.time.OffsetDateTime;

public record NoteEntity(
        Long id,
        String clientId,
        String note,
        OffsetDateTime createdAt,
        Long pointId
) {
}
