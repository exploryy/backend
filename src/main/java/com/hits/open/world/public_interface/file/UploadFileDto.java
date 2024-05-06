package com.hits.open.world.public_interface.file;

import com.hits.open.world.core.file.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

public record UploadFileDto(
        FileMetadata metadata,
        MultipartFile file
) {
}
