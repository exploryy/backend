package com.hits.open.world.core.file;

import com.hits.open.world.public_interface.file.UploadFileDto;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface FileStorageService {
    Mono<Void> uploadFile(UploadFileDto dto);

    Optional<String> getDownloadLinkByName(String name);

    Mono<Void> deleteFile(String name);
}
