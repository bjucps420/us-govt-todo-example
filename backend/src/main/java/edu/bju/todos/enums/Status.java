package edu.bju.todos.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETE("Complete");

    @Getter
    @JsonValue
    final String description;
}
