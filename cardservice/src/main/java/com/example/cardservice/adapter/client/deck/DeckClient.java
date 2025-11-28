package com.example.cardservice.adapter.client.deck;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "deck-service",
        url = "${application.config.deck-url}"
)
public interface DeckClient {

    @GetMapping("/{id}")
    Optional<DeckResponse> findByDeckId(@PathVariable String id);
}
