package com.hits.open.world.core.file;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.file.UploadFileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.time.Duration;

@Service
public class S3FileStorageService implements FileStorageService {
    private final String bucketName;
    private final String url;

    public S3FileStorageService(@Value("${amazonProperties.bucketName}") String bucketName,
                                @Value("${amazonProperties.endpointUrl}") String url
    ) {
        this.bucketName = bucketName;
        this.url = url;
    }

    @Override
    public Mono<Void> uploadFile(UploadFileDto dto) {
        return Mono.fromRunnable(() -> {
            var file = dto.file();
            var metadata = dto.metadata();
            try (var client = createClient()) {
                var putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(metadata.fileName())
                        .metadata(metadata.getMapMetadata())
                        .build();

                client.putObject(putObjectRequest,
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            } catch (Exception e) {
                throw new ExceptionInApplication("Ошибка при загрузке файлов", ExceptionType.INVALID);
            }
        });
    }

    @Override
    public String getDownloadLinkByName(String name) {
        try (var presigner = S3Presigner.builder()
                .endpointOverride(URI.create(url))
                .build()
        ) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(name)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofDays(7))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        }
    }

    @Override
    public Mono<Void> deleteFile(String name) {
        try (var client = createClient()) {
            return Mono.fromRunnable(() -> client.deleteObject(builder -> builder.bucket(bucketName).key(name)));
        } catch (Exception e) {
            throw new ExceptionInApplication("", ExceptionType.INVALID);
        }
    }

    private S3Client createClient() {
        return S3Client.builder()
                .endpointOverride(URI.create(url))
                .build();
    }
}
