package org.project.memospace.application.service.query.deck;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Deck;

import java.util.List;

@Value
public class ListDecksQuery implements Query<List<Deck>> {
}