package org.project.memospace.domain.model.browser.query;

import lombok.Builder;

import java.util.Objects;

@Builder(toBuilder = true)
public record QuerySpec(String rawQuery) {
    public QuerySpec(String rawQuery) {
        this.rawQuery = Objects.requireNonNull(rawQuery, "RawQuery cannot be null");
    }
}
