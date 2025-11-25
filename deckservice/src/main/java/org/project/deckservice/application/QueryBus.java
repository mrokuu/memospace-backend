package org.project.deckservice.application;

public interface QueryBus {

    <R> R send(Query<R> query);
}
