package com.hits.open.world.core.note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    NoteEntity save(NoteEntity noteEntity);

    void deleteById(Long id);

    List<NoteEntity> findAll();

    Long savePhoto(Long noteId);

    Optional<NoteEntity> findById(Long id);

    List<Long> getPhotosIdByNoteId(Long noteId);
}
