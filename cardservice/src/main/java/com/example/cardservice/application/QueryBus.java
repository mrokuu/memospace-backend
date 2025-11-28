package com.example.cardservice.application;

public interface QueryBus {

    <R> R send(Query<R> query);
}
