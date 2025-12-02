package org.project.deckservice.application.query;

import lombok.Value;
import org.project.deckservice.application.Query;
import org.project.deckservice.domain.model.Deck;

import java.util.List;

@Value
public class ListDecksQuery implements Query<List<Deck>> {
}
