package com.example.cardservice.application;

public interface CommandBus {

    <R> R send(Command command);
}
