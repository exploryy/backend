package com.hits.open.world.public_interface.note;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateNoteDto(
        String text,
        String latitude,
        String longitude,
        List<MultipartFile> images,
        String userId
) {
}
