package com.hits.open.world.core.note;

import com.example.open_the_world.public_.tables.records.ClientNotesRecord;
import org.jooq.RecordMapper;

public class NoteEntityMapper implements RecordMapper<ClientNotesRecord, NoteEntity> {
    @Override
    public NoteEntity map(ClientNotesRecord clientNotesRecord) {
        return new NoteEntity(
                clientNotesRecord.getNoteId(),
                clientNotesRecord.getClientId(),
                clientNotesRecord.getNote(),
                clientNotesRecord.getCreatedAt(),
                clientNotesRecord.getPointId()
        );
    }
}
