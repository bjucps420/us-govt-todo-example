package edu.bju.todos.dtos;

import edu.bju.todos.enums.Status;
import edu.bju.todos.enums.Type;
import lombok.Data;

@Data
public class TodoDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Type type;
}
