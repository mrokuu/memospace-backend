package org.project.memospace.application.service.query.note;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Note;

import java.util.UUID;

@Value
public class GetNoteQuery implements Query<Note> {
    UUID noteId;
}