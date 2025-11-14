package org.memospace.service.query.notetype;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.NoteType;

import java.util.UUID;

@Value
public class GetNoteTypeQuery implements Query<NoteType> {
    UUID noteTypeId;
}