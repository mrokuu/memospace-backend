package org.project.memospace.application.service.query.notetype;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.NoteType;

import java.util.UUID;

@Value
public class GetNoteTypeQuery implements Query<NoteType> {
    UUID noteTypeId;
}