package org.memospace.service.query.deck;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Deck;

import java.util.List;

@Value
public class ListDecksQuery implements Query<List<Deck>> {
}