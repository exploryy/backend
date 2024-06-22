package com.hits.open.world.core.note;

import com.hits.open.world.core.file.FileMetadata;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.core.multipolygon.repository.MultipolygonRepository;
import com.hits.open.world.core.route.repository.PointEntity;
import com.hits.open.world.core.route.repository.RouteRepository;
import com.hits.open.world.core.user.UserService;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.file.UploadFileDto;
import com.hits.open.world.public_interface.note.CommonNoteDto;
import com.hits.open.world.public_interface.note.CreateNoteDto;
import com.hits.open.world.public_interface.note.NoteDto;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;
    private final MultipolygonRepository multipolygonRepository;
    private final RouteRepository routeRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Long createNote(CreateNoteDto createNoteDto) {
        var pointId = routeRepository.savePoints(
                List.of(new PointEntity(createNoteDto.latitude(), createNoteDto.longitude(), null))
        ).get(0);
        var noteEntity = new NoteEntity(
                null,
                createNoteDto.userId(),
                createNoteDto.text(),
                OffsetDateTime.now(),
                pointId
        );
        var noteId = noteRepository.save(noteEntity).id();
        for (var photo : createNoteDto.images()) {
            addPhoto(noteId, photo);
        }
        return noteId;
    }

    @Transactional(readOnly = true)
    public List<CommonNoteDto> getAllNotes(String userId) {
        return noteRepository.findAll().stream()
                .filter(noteEntity -> {
                    var point = routeRepository.getPoint(noteEntity.pointId())
                            .orElseThrow(() -> new ExceptionInApplication("Point not found", ExceptionType.NOT_FOUND));
                    return inOpenArea(point, userId);
                }).map(noteEntity -> {
                    var point = routeRepository.getPoint(noteEntity.pointId())
                            .orElseThrow(() -> new ExceptionInApplication("Point not found", ExceptionType.NOT_FOUND));
                    return new CommonNoteDto(
                            noteEntity.id(),
                            point.latitude(),
                            point.longitude()
                    );
                }).toList();
    }

    @Transactional(readOnly = true)
    public NoteDto getNoteById(Long noteId) {
        var noteEntity = noteRepository.findById(noteId)
                .orElseThrow(() -> new ExceptionInApplication("Note not found", ExceptionType.NOT_FOUND));
        var point = routeRepository.getPoint(noteEntity.pointId())
                .orElseThrow(() -> new ExceptionInApplication("Point not found", ExceptionType.NOT_FOUND));
        var profile = userService.getProfile(noteEntity.clientId());
        var photos = noteRepository.getPhotosIdByNoteId(noteId)
                .stream()
                .map(photoId -> "client_note_%s_photo_%s".formatted(noteId, photoId))
                .map(fileStorageService::getDownloadLinkByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return new NoteDto(
                noteEntity.id(),
                profile,
                point.latitude(),
                point.longitude(),
                noteEntity.note(),
                noteEntity.createdAt(),
                photos
        );
    }

    private void addPhoto(Long noteId, MultipartFile image) {
        var photoId = noteRepository.savePhoto(noteId);
        var fileName = "client_note_%s_photo_%s".formatted(noteId, photoId);
        saveImage(image, fileName);
    }

    private void saveImage(MultipartFile image, String fileName) {
        var fileMetadata = new FileMetadata(
                fileName,
                image.getContentType(),
                image.getSize()
        );
        var uploadFileDto = new UploadFileDto(
                fileMetadata,
                image
        );
        fileStorageService.uploadFile(uploadFileDto).subscribe();
    }

    private boolean inOpenArea(PointEntity cord, String userId) {
        var coordinate = new Coordinate(Double.parseDouble(cord.longitude()), Double.parseDouble(cord.latitude()));
        var point = new GeometryFactory().createPoint(coordinate);
        return multipolygonRepository.isPointInPolygon(point, userId);
    }
}
