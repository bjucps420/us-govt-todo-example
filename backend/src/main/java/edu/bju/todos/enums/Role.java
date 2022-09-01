package edu.bju.todos.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Role {
    AID("Aid"),
    CLASSIFIED("Classified"),
    SECRET("Secret"),
    TOP_SECRET("Top Secret"),
    UNCLASSIFIED("Unclassified");

    @Getter
    @JsonValue
    final String description;
}
