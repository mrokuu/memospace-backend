package org.memospace.model;

import lombok.Builder;

import java.util.Objects;

@Builder(toBuilder = true)
public record RenderedClozeCard(int clozeIndex, String front, String back) {
    public RenderedClozeCard(int clozeIndex, String front, String back) {
        this.clozeIndex = clozeIndex;
        this.front = Objects.requireNonNull(front, "Front cannot be null");
        this.back = Objects.requireNonNull(back, "Back cannot be null");
    }
}