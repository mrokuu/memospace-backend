package org.memospace.service.query.notetype;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.NoteType;

import java.util.List;

@Value
public class GetAllNoteTypesQuery implements Query<List<NoteType>> {
}