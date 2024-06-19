package com.hits.open.world.public_interface.note;

import com.hits.open.world.public_interface.user.ProfileDto;

import java.time.OffsetDateTime;
import java.util.List;

public record NoteDto(
        Long id,
        ProfileDto profile,
        String latitude,
        String longitude,
        String note,
        OffsetDateTime createdAt,
        List<String> images
) {
}
