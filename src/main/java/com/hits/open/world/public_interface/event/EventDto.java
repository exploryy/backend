package com.hits.open.world.public_interface.event;

import com.hits.open.world.core.event.EventType;

public record EventDto(
        Object text,
        EventType type
) {
}
