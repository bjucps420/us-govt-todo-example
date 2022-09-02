package edu.bju.todos.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Type {
    UNCLASSIFIED("Unclassified"),
    CLASSIFIED("Classified"),
    SECRET("Secret"),
    TOP_SECRET("Top Secret");

    @Getter
    @JsonValue
    final String description;
}
