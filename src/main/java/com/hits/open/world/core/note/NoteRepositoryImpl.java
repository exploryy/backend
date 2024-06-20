package com.hits.open.world.core.note;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.Tables.CLIENT_NOTES;
import static com.example.open_the_world.public_.Tables.NOTE_PHOTO;

@Repository
@RequiredArgsConstructor
public class NoteRepositoryImpl implements NoteRepository {
    private static final NoteEntityMapper NOTE_ENTITY_MAPPER = new NoteEntityMapper();
    private final DSLContext create;

    @Override
    public NoteEntity save(NoteEntity noteEntity) {
        return create.insertInto(CLIENT_NOTES)
                .set(CLIENT_NOTES.CLIENT_ID, noteEntity.clientId())
                .set(CLIENT_NOTES.NOTE, noteEntity.note())
                .set(CLIENT_NOTES.CREATED_AT, noteEntity.createdAt())
                .set(CLIENT_NOTES.POINT_ID, noteEntity.pointId())
                .returning(CLIENT_NOTES.NOTE_ID, CLIENT_NOTES.CLIENT_ID, CLIENT_NOTES.NOTE, CLIENT_NOTES.CREATED_AT, CLIENT_NOTES.POINT_ID)
                .fetchOne(NOTE_ENTITY_MAPPER);
    }

    @Override
    public void deleteById(Long id) {
        create.deleteFrom(CLIENT_NOTES)
                .where(CLIENT_NOTES.NOTE_ID.eq(id))
                .execute();
    }

    @Override
    public List<NoteEntity> findAll() {
        return create.selectFrom(CLIENT_NOTES)
                .fetch(NOTE_ENTITY_MAPPER);
    }

    @Override
    public Long savePhoto(Long noteId) {
        return create.insertInto(NOTE_PHOTO)
                .set(NOTE_PHOTO.NOTE_ID, noteId)
                .returning(NOTE_PHOTO.PHOTO_ID)
                .fetchOne()
                .getPhotoId();
    }

    @Override
    public Optional<NoteEntity> findById(Long id) {
        return create.selectFrom(CLIENT_NOTES)
                .where(CLIENT_NOTES.NOTE_ID.eq(id))
                .fetchOptional(NOTE_ENTITY_MAPPER);
    }

    @Override
    public List<Long> getPhotosIdByNoteId(Long noteId) {
        return create.select(NOTE_PHOTO.PHOTO_ID)
                .from(NOTE_PHOTO)
                .where(NOTE_PHOTO.NOTE_ID.eq(noteId))
                .fetch(NOTE_PHOTO.PHOTO_ID);
    }

}
