package com.example.cardservice.application;

public interface CommandHandler<C extends Command, R> {

    R handle(C command);
}
