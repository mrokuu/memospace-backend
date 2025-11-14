package org.memospace.service.query.note;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Note;

import java.util.UUID;

@Value
public class GetNoteQuery implements Query<Note> {
    UUID noteId;
}